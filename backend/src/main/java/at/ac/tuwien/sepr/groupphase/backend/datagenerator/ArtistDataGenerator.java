package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;

@Profile("generateData")
@DependsOn("eventDataGenerator")
@Component
public class ArtistDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ARTISTS_TO_GENERATE = 10;
    private static final String TEST_ARTIST_FIRSTNAME = "Artist First";
    private static final String TEST_ARTIST_LASTNAME = "Artist Last";
    private static final String TEST_ARTIST_STAGENAME = "Artist STAGENAME";


    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    public ArtistDataGenerator(ArtistRepository artistRepository, EventRepository eventRepository) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void generateArtistData() {
        if (!artistRepository.findAll().isEmpty()) {
            LOGGER.debug("Artist already generated");
        } else {
            LOGGER.debug("generating {} artist entries", NUMBER_OF_ARTISTS_TO_GENERATE);
            for (int i = 0; i <= NUMBER_OF_ARTISTS_TO_GENERATE; i++) {
                Artist artist = new Artist();
                artist.setFirstName(TEST_ARTIST_FIRSTNAME);
                artist.setLastName(TEST_ARTIST_LASTNAME);
                artist.setStageName(TEST_ARTIST_STAGENAME);
                artist.setArtistType(ArtistType.SOLO);
                LOGGER.debug("saving artist {}", artist);
                artistRepository.save(artist);
            }


            Artist custom1 = new Artist();
            custom1.setFirstName("Freddie");
            custom1.setLastName("Mercury");
            custom1.setStageName("Queen");
            custom1.setArtistType(ArtistType.BAND);
            artistRepository.save(custom1);

            // Events laden und bidirektionale Beziehung korrekt setzen
            List<Event> allEvents = eventRepository.findAll();
            for (Event event : allEvents) {
                custom1.getEvents().add(event);
                event.getArtists().add(custom1);
            }

            artistRepository.save(custom1);
            eventRepository.saveAll(allEvents);

            LOGGER.debug("saved artist custom1 with id: {}", custom1.getId());

            Artist custom2 = new Artist();
            custom2.setFirstName("Elvis");
            custom2.setLastName("Presley");
            custom2.setStageName("The King");
            custom2.setArtistType(ArtistType.SOLO);
            LOGGER.debug("saving artist {}", custom2);
            artistRepository.save(custom2);
        }

    }
}
