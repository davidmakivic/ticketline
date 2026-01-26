package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TicketGenerationServiceImpl;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Profile("generateData")
@DependsOn({"performanceDataGenerator", "seatDataGenerator"})
@Component
@Order(100)
public class TicketDataGenerator implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final TicketGenerationServiceImpl ticketGenerationService;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    private final SeatRepository seatRepository;

    public TicketDataGenerator(
        TicketRepository ticketRepository,
        PerformanceRepository performanceRepository,
        TicketGenerationServiceImpl ticketGenerationService,
        EntityManager entityManager,
        TransactionTemplate transactionTemplate, SeatRepository seatRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
        this.ticketGenerationService = ticketGenerationService;
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) {
        transactionTemplate.executeWithoutResult(status -> {
            generateTicketData();
        });
    }

    private void generateTicketData() {
        if (ticketRepository.count() > 0) {
            LOGGER.debug("Tickets already generated");
            return;
        }

        long performanceCount = performanceRepository.count();
        int batchSize = 5;

        LOGGER.debug("Generating tickets for {} performances in batches", performanceCount);

        int totalTickets = 0;

        for (int page = 0; page < (performanceCount + batchSize - 1) / batchSize; page++) {
            // 1. Lade nur Performance-IDs mit echter SQL-Pagination
            List<Performance> performances = performanceRepository.findAllIds(
                PageRequest.of(page, batchSize)
            );

            List<Long> performanceIds = performances.stream()
                .map(Performance::getId)
                .toList();

            // 2. Eager-load Hall + Sectors für diese IDs
            performances = performanceRepository.findByIdsWithHallAndSectors(performanceIds);

            for (Performance performance : performances) {
                if (ticketRepository.existsByPerformanceId(performance.getId())) {
                    continue;
                }
                List<Seat> seats = seatRepository.findByHallIdWithSector(performance.getHall().getId());
                ticketGenerationService.generateTicketsForPerformanceWithSeats(performance, seats);

                totalTickets += seats.size();


            }

            entityManager.flush();
            entityManager.clear();

            LOGGER.debug("Processed page {} of {}", page + 1, (performanceCount + batchSize - 1) / batchSize);
        }

        LOGGER.debug("Ticket generation complete – created ~{} tickets", totalTickets);
    }


}
