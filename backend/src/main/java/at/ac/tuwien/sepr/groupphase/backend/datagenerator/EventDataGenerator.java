package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@Component
public class EventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ClassPathResource[] MUSICAL_IMAGES = {
        new ClassPathResource("images/phantom-der-oper.jpg"),
        new ClassPathResource("images/LionKing.jpg"),
        new ClassPathResource("images/abba-mamma-mia-tickets-2024-m.jpg"),
        new ClassPathResource("images/airbourne-gutsy-tickets-2025-m.jpg"),
        new ClassPathResource("images/amaranthe-epica-tickets-2025-m.jpg"),
        new ClassPathResource("images/electric-love-2026-SHM-tickets-1030-m.jpg"),
        new ClassPathResource("images/fallback-image-event.jpg"),
        new ClassPathResource("images/Lords-Of-The-Sound-2025-tickets-m.jpg"),
        new ClassPathResource("images/Die_Paldauer_-_Bis_ans_Ende_der_Welt_c_Karl_Schroter_222.jpg")

    };
    private static final ClassPathResource[] CONCERT_IMAGES = {
        new ClassPathResource("images/Rock-am-Ring-24.png"),
        new ClassPathResource("images/jazz.jpg"),
        new ClassPathResource("images/Symphonic-Rock-Night.jpg"),
        new ClassPathResource("images/pop-legends.jpeg"),
        new ClassPathResource("images/electric-love-2026-SHM-tickets-1030-m.jpg"),
        new ClassPathResource("images/kings-of-leon-tickets-2025-m.jpg"),
        new ClassPathResource("images/lenny-kravitz-tickets-2026-m.jpg"),
        new ClassPathResource("images/Leprous-2026-tickets-c-Photo-by-Tomasz-Gottryd-m.jpg"),
        new ClassPathResource("images/Lars_Eidinger_-_My_Way_c_Roman_Goebel_222.jpg"),
    };
    private static final ClassPathResource[] FESTIVAL_IMAGES = {
        new ClassPathResource("images/Kultursommer-Wien-2025-praterwiese.jpg"),
        new ClassPathResource("images/Indie-Summer-Festival.jpg"),
        new ClassPathResource("images/Napalm-Death-2026-tickets-m.jpg"),
        new ClassPathResource("images/One-Love-Festival-2026-tickets-m.jpg"),
        new ClassPathResource("images/nova-rock-2026-tickets-m.jpg"),
        new ClassPathResource("images/heaven-shall-burn-2026-tickets-m.jpg"),
        new ClassPathResource("images/Jason-Derulo-The-Last-Dance-World-Tour-2026-tickets-0116-m.jpg"),
        new ClassPathResource("images/onerepublic-from-europe-with-love-tickets-2026-m.jpg"),


    };

    private final EventRepository eventRepository;

    public EventDataGenerator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void generateEventData() {
        if (!eventRepository.findAll().isEmpty()) {
            LOGGER.debug("Events already generated — skipping");
            return;
        }

        LOGGER.debug("Generating realistic demo events");



        Event e1 = new Event("Phantom der Oper",
            "Das weltberühmte Musical über das mysteriöse Phantom im Opernhaus.",
            EventType.MUSICAL,
            160);
        loadImageFromFile(e1, "src/main/resources/images/phantom-der-oper.jpg");

        Event e2 = new Event("König der Löwen",
            "Ein bewegendes Musical rund um Simba und sein Schicksal.",
            EventType.MUSICAL,
            150);
        loadImageFromFile(e2, "src/main/resources/images/LionKing.jpg");

        Event e3 = new Event("Rock am Ring – Live Night",
            "Ein energiegeladenes Konzert mit international bekannten Rockbands.",
            EventType.CONCERT,
            180);
        loadImageFromFile(e3, "src/main/resources/images/Rock-am-Ring-24.png");

        Event e4 = new Event("Vienna Jazz Classics",
            "Ein Jazzkonzert mit klassischen und modernen Jazz-Interpretationen.",
            EventType.CONCERT,
            120);
        loadImageFromFile(e4, "src/main/resources/images/jazz.jpg");

        Event e5 = new Event("Sommer Festival 2025",
            "Ein großes Outdoor-Festival mit verschiedenen Künstlern und Acts.",
            EventType.FESTIVAL,
            300);
        loadImageFromFile(e5, "src/main/resources/images/Kultursommer-Wien-2025-praterwiese.jpg");

        Event e6 = new Event("Elektro Beats Festival",
            "Ein Festival für elektronische Musik, DJs und beeindruckende Lichtshows.",
            EventType.FESTIVAL,
            360);
        loadImageFromFile(e6, new ClassPathResource("images/maxresdefault.jpg"));

        Event e7 = new Event("Symphonic Rock Night",
            "Ein außergewöhnliches Konzert, das Rock und Orchester kombiniert.",
            EventType.CONCERT,
            140);
        loadImageFromFile(e7, "src/main/resources/images/Symphonic-Rock-Night.jpg");

        Event e8 = new Event("Mamma Mia! – Das Musical",
            "Das beliebte Musical basierend auf den größten Hits von ABBA.",
            EventType.MUSICAL,
            135);
        loadImageFromFile(e8, "src/main/resources/images/mamma-mia.jpg");

        Event e9 = new Event("Pop Legends Live",
            "Ein Popkonzert der größten Chartstürmer des Jahres.",
            EventType.CONCERT,
            110);
        loadImageFromFile(e9, "src/main/resources/images/pop-legends.jpeg");

        Event e10 = new Event("Indie Summer Festival",
            "Ein Festival mit bekannten Indie-Bands und Newcomern.",
            EventType.FESTIVAL,
            280);
        loadImageFromFile(e10, new ClassPathResource("images/Indie-Summer-Festival.jpg"));
        List<Event> initialEvents = List.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
        eventRepository.saveAll(initialEvents);
        eventRepository.flush();

        // Restliche Events in Batches generieren
        int batchSize = 20;
        for (int i = 11; i <= 200; i += batchSize) {
            List<Event> batch = new ArrayList<>();
            int toIndex = Math.min(i + batchSize - 1, 200);

            for (int j = i; j <= toIndex; j++) {
                EventType type = EventType.values()[j % EventType.values().length];
                int duration = 90 + (j % 13) * 10;
                Event generated = new Event(
                    "Demo Event " + j,
                    "Automatisch generiertes Event Nummer " + j,
                    type, duration);

                // Nur die ersten 30 Events bekommen Bilder
                if (j <= 30) {
                    loadImageFromFile(generated, pickImageForType(type, j));
                }

                batch.add(generated);
            }

            eventRepository.saveAll(batch);
            eventRepository.flush();
            batch.clear();
            LOGGER.debug("Saved events {} to {}", i, toIndex);
        }

        LOGGER.debug("200 demo events generated successfully");
    }

    private ClassPathResource pickImageForType(EventType type, int index) {
        return switch (type) {
            case MUSICAL -> MUSICAL_IMAGES[index % MUSICAL_IMAGES.length];
            case CONCERT -> CONCERT_IMAGES[index % CONCERT_IMAGES.length];
            case FESTIVAL -> FESTIVAL_IMAGES[index % FESTIVAL_IMAGES.length];
        };
    }


    private void loadImageFromFile(Event event, ClassPathResource img) {
        try (InputStream in = img.getInputStream()) {
            byte[] bytes = in.readAllBytes();
            event.setImageData(new SerialBlob(bytes));
            event.setImageContentType("image/jpeg");
        } catch (IOException | SQLException e) {
            LOGGER.warn("Could not load image from {}: {}", img.getFilename(), e.getMessage());
        }
    }


}
