package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketGenerationService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import at.ac.tuwien.sepr.groupphase.backend.repository.projection.SeatPriceProjection;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketGenerationServiceImpl implements TicketGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int BATCH_SIZE = 50;

    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final EntityManager entityManager;

    public TicketGenerationServiceImpl(
        SeatRepository seatRepository,
        TicketRepository ticketRepository,
        EntityManager entityManager
    ) {
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void generateTicketsForPerformance(Performance performance) {
        Long performanceId = performance.getId();
        Long hallId = performance.getHall().getId();
        Long basePriceCents = performance.getBasePriceCents();

        LOGGER.info("Generating tickets for performance id={} in hall id={}", performanceId, hallId);

        List<SeatPriceProjection> seats = seatRepository.findSeatPriceDataByHallId(hallId);

        final int chunk = 200;
        int i = 0;

        Performance perfRef = entityManager.getReference(Performance.class, performanceId);

        for (SeatPriceProjection s : seats) {
            Long seatId = s.getSeatId();
            Long priceInCents = calculatePrice(basePriceCents, s.getPriceFactor());

            Ticket ticket = new Ticket();
            ticket.setPerformance(perfRef);
            ticket.setSeat(entityManager.getReference(Seat.class, seatId));
            ticket.setPriceFinalCents(priceInCents);
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setReservedUntil(null);
            ticket.setReservedByUserId(null);

            entityManager.persist(ticket);

            i++;
            if (i % chunk == 0) {
                entityManager.flush();
                entityManager.clear();

                perfRef = entityManager.getReference(Performance.class, performanceId);
            }
        }

        entityManager.flush();
        entityManager.clear();

        LOGGER.info("Created {} tickets for performance id={}", seats.size(), performanceId);
    }

    @Transactional
    @Override
    public void generateTicketsForPerformanceWithSeats(Performance performance, List<Seat> seats) {
        LOGGER.debug("Generating {} tickets for performance id={}", seats.size(), performance.getId());

        List<Ticket> ticketBatch = new ArrayList<>(BATCH_SIZE);

        for (Seat seat : seats) {
            Long priceInCents = calculatePrice(
                performance.getBasePriceCents(),
                seat.getSector().getPriceCategory().getPrice()
            );

            Ticket ticket = new Ticket();
            ticket.setPerformance(performance);
            ticket.setSeat(seat);
            ticket.setPriceFinalCents(priceInCents);
            ticket.setStatus(TicketStatus.AVAILABLE);

            ticketBatch.add(ticket);

            if (ticketBatch.size() >= BATCH_SIZE) {
                ticketRepository.saveAll(ticketBatch);
                entityManager.flush();
                entityManager.clear();
                ticketBatch.clear();
            }
        }

        if (!ticketBatch.isEmpty()) {
            ticketRepository.saveAll(ticketBatch);
            entityManager.flush();
            entityManager.clear();
        }

        LOGGER.debug("Created {} tickets for performance id={}", seats.size(), performance.getId());
    }


    private Long calculatePrice(Long basePriceCents, double categoryPriceMultiplier) {
        if (basePriceCents == null) {
            return 0L;
        }
        return (long) (basePriceCents * categoryPriceMultiplier);
    }
}
