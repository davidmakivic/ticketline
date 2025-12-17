package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;

import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallServiceImpl;
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

    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;
    private final ObjectMapper objectMapper;
    private final HallServiceImpl hallServiceImpl;

    public HallDataGenerator(VenueRepository venueRepository,
                             HallRepository hallRepository,
                             ObjectMapper objectMapper, HallServiceImpl hallServiceImpl) {
        this.venueRepository = venueRepository;
        this.hallRepository = hallRepository;
        this.objectMapper = objectMapper;
        this.hallServiceImpl = hallServiceImpl;
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

        // Layout wird einmal geladen und anschließend für jede Hall gesetzt
        JsonNode layout1 = loadLayout("hall1_layout.json");
        JsonNode layout2 = loadLayout("hall2_layout.json");

        LOGGER.debug("Generating halls for venues");

        for (Venue venue : venues) {

            if (venue.getName() == "Graz Arena") {
                Hall mnHall = new Hall();
                mnHall.setName("Saal A");
                mnHall.setVenue(venue);
                mnHall.setLayoutMetadata(layout2);

                Hall slHall = new Hall();
                slHall.setName("Saal B");
                slHall.setVenue(venue);
                slHall.setLayoutMetadata(layout2);

                hallRepository.save(mnHall);
                hallRepository.save(slHall);
            } else {

                Hall mainHall = new Hall();
                mainHall.setName("Saal A");
                mainHall.setVenue(venue);
                mainHall.setLayoutMetadata(layout1);

                Hall smallHall = new Hall();
                smallHall.setName("Saal B");
                smallHall.setVenue(venue);
                smallHall.setLayoutMetadata(layout1);

                hallRepository.save(mainHall);
                hallRepository.save(smallHall);
            }
        }

        LOGGER.debug("Hall generation complete");
    }
}
