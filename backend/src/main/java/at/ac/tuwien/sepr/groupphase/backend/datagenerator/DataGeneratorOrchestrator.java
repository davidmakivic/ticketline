package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class DataGeneratorOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PriceCategoryDataGenerator priceCategoryDataGenerator;
    private final UserDataGenerator userDataGenerator;
    private final VenueDataGenerator venueDataGenerator;
    private final EventDataGenerator eventDataGenerator;
    private final HallDataGenerator hallDataGenerator;
    private final SectorDataGenerator sectorDataGenerator;
    private final SeatDataGenerator seatDataGenerator;
    private final PerformanceDataGenerator performanceDataGenerator;
    private final TicketDataGenerator ticketDataGenerator;
    private final OrderDataGenerator orderDataGenerator;
    private final ArtistDataGenerator artistDataGenerator;
    private final NewsDataGenerator newsDataGenerator;
    private final ApplicationContext applicationContext;

    public DataGeneratorOrchestrator(
        PriceCategoryDataGenerator priceCategoryDataGenerator,
        UserDataGenerator userDataGenerator,
        VenueDataGenerator venueDataGenerator,
        EventDataGenerator eventDataGenerator,
        HallDataGenerator hallDataGenerator,
        SectorDataGenerator sectorDataGenerator,
        SeatDataGenerator seatDataGenerator,
        PerformanceDataGenerator performanceDataGenerator,
        TicketDataGenerator ticketDataGenerator,
        OrderDataGenerator orderDataGenerator,
        ArtistDataGenerator artistDataGenerator,
        NewsDataGenerator newsDataGenerator,
        ApplicationContext applicationContext
    ) {
        this.priceCategoryDataGenerator = priceCategoryDataGenerator;
        this.userDataGenerator = userDataGenerator;
        this.venueDataGenerator = venueDataGenerator;
        this.eventDataGenerator = eventDataGenerator;
        this.hallDataGenerator = hallDataGenerator;
        this.sectorDataGenerator = sectorDataGenerator;
        this.seatDataGenerator = seatDataGenerator;
        this.performanceDataGenerator = performanceDataGenerator;
        this.ticketDataGenerator = ticketDataGenerator;
        this.orderDataGenerator = orderDataGenerator;
        this.artistDataGenerator = artistDataGenerator;
        this.newsDataGenerator = newsDataGenerator;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void generateAllData() {
        try {
            LOGGER.info("Starting data generation orchestration...");

            // Ebene 1: Keine Abhängigkeiten
            LOGGER.debug("Stage 1: Generating price categories, users, venues, and events...");
            priceCategoryDataGenerator.generatePriceCategories();
            userDataGenerator.generateUser();
            venueDataGenerator.generateVenues();
            eventDataGenerator.generateEventData();

            // Ebene 2: Abhängig von Ebene 1
            LOGGER.debug("Stage 2: Generating halls and artists...");
            hallDataGenerator.generateHallData();
            artistDataGenerator.generateArtistData();

            // Ebene 3: Abhängig von Halls und PriceCategories
            LOGGER.debug("Stage 3: Generating sectors...");
            sectorDataGenerator.generateSectors();

            // Ebene 4: Abhängig von Sectors
            LOGGER.debug("Stage 4: Generating seats...");
            seatDataGenerator.generateSeats();

            // Ebene 5: Abhängig von Events und Halls
            LOGGER.debug("Stage 5: Generating performances...");
            performanceDataGenerator.generatePerformanceData();

            // Ebene 6: Abhängig von Performances und Seats
            LOGGER.debug("Stage 6: Generating tickets...");
            ticketDataGenerator.generateTicketData();

            // Ebene 7: Abhängig von Users und Tickets
            LOGGER.debug("Stage 7: Generating orders...");
            orderDataGenerator.generateOrders();

            // Ebene 8: Unabhängig
            LOGGER.debug("Stage 8: Generating news...");
            newsDataGenerator.generateMessage();

            LOGGER.info("All data generation completed successfully. Shutting down application.");

            // Anwendung herunterfahren, falls konfiguriert
            if (isShutdownAfterGenerationEnabled()) {
                SpringApplication.exit(applicationContext, () -> 0);
            }

        } catch (Exception e) {
            LOGGER.error("Error during data generation", e);
            SpringApplication.exit(applicationContext, () -> 1);
        }
    }

    private boolean isShutdownAfterGenerationEnabled() {
        String shutdownProperty = System.getProperty("app.shutdown-after-data-generation", "false");
        return Boolean.parseBoolean(shutdownProperty);
    }
}
