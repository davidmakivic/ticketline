package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
@DependsOn({
    "eventDataGenerator",
    "artistDataGenerator",
    "venueDataGenerator",
    "hallDataGenerator",
    "seatDataGenerator",
    "performanceDataGenerator",
    "userDataGenerator",
    "orderDataGenerator",
    "ticketDataGenerator",
    "newsDataGenerator",
    "priceCategoryDataGenerator",
    "sectorDataGenerator"
})
public class DataGeneratorOrchestrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ApplicationContext applicationContext;

    @Value("${app.shutdown-after-data-generation:false}")
    private boolean shutdownAfterGeneration;

    public DataGeneratorOrchestrator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void onApplicationStart() {
        if (shutdownAfterGeneration) {
            LOGGER.info("All data generation completed. Shutting down application.");
            SpringApplication.exit(applicationContext, () -> 0);
        } else {
            LOGGER.debug("Data generation completed (shutdown disabled)");
        }
    }
}
