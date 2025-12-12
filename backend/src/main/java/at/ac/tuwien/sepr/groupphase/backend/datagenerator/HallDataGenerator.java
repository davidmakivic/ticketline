package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@DependsOn("venueDataGenerator")
@Component
public class HallDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;

    public HallDataGenerator(VenueRepository venueRepository, HallRepository hallRepository) {
        this.venueRepository = venueRepository;
        this.hallRepository = hallRepository;
    }

    @PostConstruct
    public void generateHalls() {
        if (!hallRepository.findAll().isEmpty()) {
            LOG.debug("Halls already generated — skipping");
            return;
        }

        LOG.debug("Generating halls for venues");

        for (Venue venue : venueRepository.findAll()) {
            Hall mainHall = new Hall();
            mainHall.setName(venue.getName() + " - Großer Saal");
            mainHall.setVenue(venue);

            Hall smallHall = new Hall();
            smallHall.setName(venue.getName() + " - Kleiner Saal");
            smallHall.setVenue(venue);

            hallRepository.save(mainHall);
            hallRepository.save(smallHall);
        }

        LOG.debug("Hall generation complete");
    }
}
