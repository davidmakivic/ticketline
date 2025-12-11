package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Profile("generateData")
@DependsOn("venueDataGenerator")
@Component
public class HallDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_HALLS_TO_GENERATE = 10;
    private static final String[] TEST_HALL_NAME = new String[] {
        "HALL NAME 1", "HALL NAME 2", "HALL NAME 3", "HALL NAME 4", "HALL NAME 5",
        "HALL NAME 6", "HALL NAME 7", "HALL NAME 8", "HALL NAME 9", "HALL NAME 10"
    };

    private final HallRepository hallRepository;
    private final VenueRepository venueRepository;
    private final ObjectMapper objectMapper;

    public HallDataGenerator(HallRepository hallRepository, VenueRepository venueRepository, ObjectMapper objectMapper) {
        this.hallRepository = hallRepository;
        this.venueRepository = venueRepository;
        this.objectMapper = objectMapper;
    }

    private JsonNode loadLayout(String fileName) {
        try (InputStream in = new ClassPathResource("layouts/" + fileName).getInputStream()) {
            return objectMapper.readTree(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load layout file: " + fileName, e);
        }
    }

    @PostConstruct
    public void generateHallData() {
        if (!hallRepository.findAll().isEmpty()) {
            LOGGER.debug("Halls already generated — skipping");
            return;
        }

        List<Venue> venues = venueRepository.findAll();
        if (venues.isEmpty()) {
            LOGGER.error("No venues found — cannot generate halls!");
            return;
        }

        LOGGER.debug("Generating {} hall entries", NUMBER_OF_HALLS_TO_GENERATE);

        for (int i = 0; i < NUMBER_OF_HALLS_TO_GENERATE; i++) {
            Venue venue = venues.get(i % venues.size());

            Hall hall = new Hall();
            hall.setName(TEST_HALL_NAME[i]);
            hall.setVenue(venue);

            // We are only setting the hall_layout json for the hall with name "HALL NAME 1"
            if ("HALL NAME 1".equals(TEST_HALL_NAME[i])) {
                hall.setLayoutMetadata(loadLayout("hall1_layout.json"));
            }

            hallRepository.save(hall);
        }
    }
}
