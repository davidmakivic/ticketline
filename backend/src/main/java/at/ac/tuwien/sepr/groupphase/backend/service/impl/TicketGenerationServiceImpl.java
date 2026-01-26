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
        if (ticketRepository.existsByPerformanceId(performance.getId())) {
            LOGGER.debug("Tickets for performance {} already exist – skipping", performance.getId());
            return;
        }
        bulkInsert(performance);
    }

    @Override
    @Transactional
    public void generateTicketsForPerformanceWithSeats(Performance performance, List<Seat> seats) {
        if (ticketRepository.existsByPerformanceId(performance.getId())) {
            LOGGER.debug("Tickets for performance {} already exist – skipping", performance.getId());
            return;
        }
        // Seats werden hier nicht einzeln geschrieben, sondern nur für Preisinfo genutzt (über SQL-Join).
        bulkInsert(performance);
    }

    private void bulkInsert(Performance performance) {
        int inserted = ticketRepository.bulkInsertForPerformance(
            performance.getId(),
            performance.getHall().getId(),
            performance.getBasePriceCents()
        );
        LOGGER.debug("Bulk-inserted {} tickets for performance {}", inserted, performance.getId());
    }



    private Long calculatePrice(Long basePriceCents, double categoryPriceMultiplier) {
        if (basePriceCents == null) {
            return 0L;
        }
        return (long) (basePriceCents * categoryPriceMultiplier);
    }
}
