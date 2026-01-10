package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Profile("generateData")
@Component
public class ArtistDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
            return;
        }

        LOGGER.debug("generating artist entries");

        Artist freddie = new Artist();
        freddie.setFirstName("Freddie");
        freddie.setLastName("Mercury");
        freddie.setStageName("Queen");
        freddie.setArtistType(ArtistType.BAND);
        loadImageFromFile(freddie, "src/main/resources/images/freddie-mercury.jpg");
        artistRepository.save(freddie);

        Artist elvis = new Artist();
        elvis.setFirstName("Elvis");
        elvis.setLastName("Presley");
        elvis.setStageName("The King");
        elvis.setArtistType(ArtistType.SOLO);
        loadImageFromFile(elvis, "src/main/resources/images/Elvis_Presley.jpg");
        artistRepository.save(elvis);

        Artist bono = new Artist();
        bono.setFirstName("Paul");
        bono.setLastName("Hewson");
        bono.setStageName("Bono");
        bono.setArtistType(ArtistType.BAND);
        loadImageFromFile(bono, "src/main/resources/images/Bono.jpg");
        artistRepository.save(bono);

        Artist davidBowie = new Artist();
        davidBowie.setFirstName("David");
        davidBowie.setLastName("Bowie");
        davidBowie.setStageName("David Bowie");
        davidBowie.setArtistType(ArtistType.SOLO);
        loadImageFromFile(davidBowie, "src/main/resources/images/bowie.jpg");
        artistRepository.save(davidBowie);

        Artist mickJagger = new Artist();
        mickJagger.setFirstName("Mick");
        mickJagger.setLastName("Jagger");
        mickJagger.setStageName("The Rolling Stones");
        mickJagger.setArtistType(ArtistType.BAND);
        loadImageFromFile(mickJagger, "src/main/resources/images/mick-jagger.jpg");
        artistRepository.save(mickJagger);

        // Events laden und Verbindungen setzen
        List<Event> allEvents = eventRepository.findAll();

        if (allEvents.size() >= 3) {
            allEvents.get(2).getArtists().add(freddie); // Rock am Ring
            allEvents.get(2).getArtists().add(mickJagger);
            freddie.getEvents().add(allEvents.get(2));
            mickJagger.getEvents().add(allEvents.get(2));

            allEvents.get(3).getArtists().add(bono); // Vienna Jazz Classics
            bono.getEvents().add(allEvents.get(3));

            allEvents.get(4).getArtists().add(elvis); // Sommer Festival
            elvis.getEvents().add(allEvents.get(4));

            allEvents.get(5).getArtists().add(davidBowie); // Elektro Beats Festival
            davidBowie.getEvents().add(allEvents.get(5));

            allEvents.get(6).getArtists().add(freddie); // Symphonic Rock Night
            allEvents.get(6).getArtists().add(bono);
            freddie.getEvents().add(allEvents.get(6));
            bono.getEvents().add(allEvents.get(6));

            allEvents.get(8).getArtists().add(elvis); // Pop Legends Live
            elvis.getEvents().add(allEvents.get(8));
        }

        artistRepository.saveAll(List.of(freddie, elvis, bono, davidBowie, mickJagger));
        eventRepository.saveAll(allEvents);

        LOGGER.debug("Artist data generated and linked to events successfully");
    }

    private void loadImageFromFile(Artist artist, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                byte[] imageData = Files.readAllBytes(path);
                artist.setImageData(imageData);
                artist.setImageContentType("image/jpeg");
            }
        } catch (IOException e) {
            LOGGER.warn("Could not load image from {}: {}", filePath, e.getMessage());
        }
    }
}
