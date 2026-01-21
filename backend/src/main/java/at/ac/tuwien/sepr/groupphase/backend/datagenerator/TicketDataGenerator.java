package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@DependsOn({"performanceDataGenerator", "seatDataGenerator"})
@Component
public class TicketDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    public TicketDataGenerator(
        TicketRepository ticketRepository,
        PerformanceRepository performanceRepository,
        SeatRepository seatRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
    }

    @PostConstruct
    @Transactional
    public void generateTicketData() {
        if (!ticketRepository.findAll().isEmpty()) {
            LOGGER.debug("Tickets already generated");
            return;
        }

        List<Performance> performances = performanceRepository.findAll();
        if (performances.isEmpty()) {
            LOGGER.warn("No performances available – cannot generate tickets");
            return;
        }

        LOGGER.debug("Generating tickets: one ticket per seat & performance");

        int created = 0;
        int batchSize = 50; // Batch-Verarbeitung

        for (Performance performance : performances) {
            Long hallId = performance.getHall().getId();
            List<Seat> seatsForHall = seatRepository.findByHallIdWithSector(hallId);

            if (seatsForHall.isEmpty()) {
                LOGGER.warn("No seats found for hall {} (performance id = {})",
                    hallId, performance.getId());
                continue;
            }

            List<Ticket> batch = new ArrayList<>(batchSize);

            for (Seat seat : seatsForHall) {
                Ticket ticket = new Ticket();
                ticket.setPerformance(performance);
                ticket.setSeat(seat);
                ticket.setStatus(TicketStatus.AVAILABLE);

                SectorType sectorType = seat.getSector().getType();
                Long basePrice = performance.getBasePriceCents();
                double multiplier = sectorMultiplier(sectorType);
                Long finalPrice = Math.round(basePrice * multiplier);

                ticket.setPriceFinalCents(finalPrice);
                batch.add(ticket);

                // Batch speichern und Speicher freigeben
                if (batch.size() >= batchSize) {
                    ticketRepository.saveAll(batch);
                    ticketRepository.flush();
                    batch.clear();
                    created += batchSize;
                }
            }

            // Restliche Tickets speichern
            if (!batch.isEmpty()) {
                ticketRepository.saveAll(batch);
                ticketRepository.flush();
                created += batch.size();
                batch.clear();
            }
        }

        LOGGER.debug("Ticket generation complete – created {} tickets", created);
    }


    private double sectorMultiplier(SectorType sectorType) {
        return switch (sectorType) {
            case STANDING -> 1.0;
            case SEATED -> 1.5;
            case VIP -> 2.0;
        };
    }

}

