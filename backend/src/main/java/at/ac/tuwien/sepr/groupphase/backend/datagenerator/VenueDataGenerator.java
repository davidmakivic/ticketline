package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class VenueDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final VenueRepository venueRepository;

    public VenueDataGenerator(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @PostConstruct
    public void generateVenues() {
        if (!venueRepository.findAll().isEmpty()) {
            LOG.debug("Venues already generated — skipping");
            return;
        }

        LOG.debug("Generating demo venues");

        Venue v1 = new Venue("Wiener Stadthalle", "Roland-Rainer-Platz 1",
            "Wien", "Austria", "1150");
        Venue v2 = new Venue("Graz Arena", "Musterstraße 5",
            "Graz", "Austria", "8010");
        Venue v3 = new Venue("Linz Music Hall", "Donauufer 10",
            "Linz", "Austria", "4020");
        Venue v4 = new Venue("Salzburg Dome", "Domplatz 1",
            "Salzburg", "Austria", "5020");

        venueRepository.save(v1);
        venueRepository.save(v2);
        venueRepository.save(v3);
        venueRepository.save(v4);

        for (int i = 0; i < 21; i++) {
            venueRepository.save(new Venue("Demo Venue " + (i + 1), "Demo Street " + (i + 1),
                "Demo City " + (i + 1), "Austria", "9000" + (i + 1)));
        }

        LOG.debug("Venue generation complete");
    }

}
