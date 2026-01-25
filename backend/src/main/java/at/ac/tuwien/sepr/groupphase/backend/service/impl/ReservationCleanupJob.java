package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationCleanupJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationCleanupJob.class);

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;

    public ReservationCleanupJob(ReservationRepository reservationRepository, TicketRepository ticketRepository) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
    }

    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void cleanup() {
        Instant now = Instant.now();
        Duration cutoff = Duration.ofMinutes(30);

        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation r : reservations) {
            List<Ticket> tickets = r.getTickets();
            if (tickets == null || tickets.isEmpty()) {
                continue;
            }

            List<Ticket> toRelease = new ArrayList<>();

            for (Ticket t : tickets) {
                if (t.getPerformance() == null || t.getPerformance().getStartTime() == null) {
                    continue;
                }
                Instant start = t.getPerformance().getStartTime().toInstant();
                Instant latestValid = start.minus(cutoff);

                if (!now.isBefore(latestValid)) {
                    toRelease.add(t);
                }
            }

            if (toRelease.isEmpty()) {
                continue;
            }

            for (Ticket t : toRelease) {
                t.setStatus(TicketStatus.AVAILABLE);
                t.setReservedByUserId(null);
                t.setReservedUntil(null);
            }
            ticketRepository.saveAll(toRelease);

            ticketRepository.detachFromReservation(
                toRelease.stream().map(Ticket::getId).toList()
            );

            tickets.removeAll(toRelease);

            if (tickets.isEmpty()) {
                reservationRepository.delete(r);
            } else {
                reservationRepository.save(r);
            }
        }

        LOGGER.debug("Reservation cleanup done");
    }
}
