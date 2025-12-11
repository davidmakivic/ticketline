package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class VenueDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_VENUES_TO_GENERATE = 5;
    private static final String[] TEST_VENUE_NAME = new String[] {"VENUE NAME 1", "VENUE NAME 2", "VENUE NAME 3", "VENUE NAME 4", "VENUE NAME 5"};
    private static final String[] TEST_VENUE_CITY = new String[] {"VENUE CITY 1", "VENUE CITY 2", "VENUE CITY 3", "VENUE CITY 4", "VENUE CITY 5"};
    private static final String[] TEST_VENUE_COUNTRY = new String[] {"VENUE COUNTRY 1", "VENUE COUNTRY 2", "VENUE COUNTRY 3",  "VENUE COUNTRY 4",  "VENUE COUNTRY 5"};
    private static final String[] TEST_VENUE_STREET = new String[] {"VENUE STREET 1", "VENUE STREET 2", "VENUE STREET 3", "VENUE STREET 4",  "VENUE STREET 5"};
    private static final String[] TEST_VENUE_POSTALCODE = new String[] {"VENUE POSTAL CODE 1", "VENUE POSTAL CODE 2", "VENUE POSTAL CODE 3", "VENUE POSTAL CODE 4", "VENUE POSTAL CODE 5"};

    private final VenueRepository venueRepository;

    public VenueDataGenerator(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @PostConstruct
    public void generateVenueData() {
        if (!venueRepository.findAll().isEmpty()) {
            LOGGER.debug("Venue already generated");
        } else {
            LOGGER.debug("generating {} venue entries", NUMBER_OF_VENUES_TO_GENERATE);

            for (int i = 0; i < NUMBER_OF_VENUES_TO_GENERATE; i++) {
                Venue venue = new Venue();
                venue.setName(TEST_VENUE_NAME[i]);
                venue.setCity(TEST_VENUE_CITY[i]);
                venue.setCountry(TEST_VENUE_COUNTRY[i]);
                venue.setStreet(TEST_VENUE_STREET[i]);
                venue.setPostalCode(TEST_VENUE_POSTALCODE[i]);
                LOGGER.debug("saving venue {}", venue);
                venueRepository.save(venue);
            }
        }
    }

}
