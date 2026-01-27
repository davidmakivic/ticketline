package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TicketGenerationServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("generateData")
@DependsOn({"performanceDataGenerator", "seatDataGenerator"})
@Component
@Order(100)
public class TicketDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final TicketGenerationServiceImpl ticketGenerationService;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    private final SeatRepository seatRepository;

    private final Map<Long, List<Seat>> seatCache = new HashMap<>();

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

    @PostConstruct
    private void generateTickets() {
        if (ticketRepository.count() > 0) {
            LOGGER.debug("Tickets already generated");
            return;
        }

        LOGGER.debug("Generating tickets for all performances in batches");

        long totalPerformances = performanceRepository.count();
        int pageSize = 20; // Anzahl pro Seite anpassen
        int totalPages = (int) Math.ceil((double) totalPerformances / pageSize);

        for (int page = 0; page < totalPages; page++) {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());
            List<Performance> performances = performanceRepository.findAllIds(pageable);

            if (performances.isEmpty()) {
                continue;
            }

            List<Long> ids = performances.stream()
                .map(Performance::getId)
                .toList();

            List<Performance> performancesWithDetails = performanceRepository.findByIdsWithHallAndSectors(ids);

            for (Performance performance : performancesWithDetails) {
                ticketGenerationService.generateTicketsForPerformance(performance);
                LOGGER.debug("Tickets generated for performance {}", performance.getId());
            }

            LOGGER.debug("Processed page {} of {}", page + 1, totalPages);
        }

        LOGGER.debug("Ticket generation complete for all performances");
    }




}
