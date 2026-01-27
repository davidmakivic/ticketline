package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
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
    private final SectorRepository sectorRepository;

    public TicketGenerationServiceImpl(
        SeatRepository seatRepository,
        TicketRepository ticketRepository,
        EntityManager entityManager, SectorRepository sectorRepository
    ) {
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.entityManager = entityManager;
        this.sectorRepository = sectorRepository;
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
        List<Sector> sectors = sectorRepository.findByHallId(performance.getHall().getId());
        int totalInserted = 0;
        for (Sector sector : sectors) {
            int inserted = ticketRepository.bulkInsertForPerformanceAndSector(
                performance.getId(),
                sector.getId(),
                performance.getBasePriceCents()
            );
            totalInserted += inserted;
            LOGGER.debug("Bulk-inserted {} tickets for performance {} and sector {}", inserted, performance.getId(), sector.getId());
        }
        LOGGER.debug("Total bulk-inserted {} tickets for performance {}", totalInserted, performance.getId());
    }


    private Long calculatePrice(Long basePriceCents, double categoryPriceMultiplier) {
        if (basePriceCents == null) {
            return 0L;
        }
        return (long) (basePriceCents * categoryPriceMultiplier);
    }
}
