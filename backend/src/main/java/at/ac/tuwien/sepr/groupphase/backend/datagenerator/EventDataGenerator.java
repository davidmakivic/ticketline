package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
        loadImageFromFile(e6, "src/main/resources/images/Elektro-Beats-Festival.jpg");

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
        loadImageFromFile(e10, "src/main/resources/images/Indie-Summer-Festival.jpg");

        eventRepository.saveAll(List.of(
            e1, e2, e3, e4, e5, e6, e7, e8, e9, e10
        ));

        LOGGER.debug("10 demo events generated successfully");
    }

    private void loadImageFromFile(Event event, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                byte[] imageData = Files.readAllBytes(path);
                event.setImageData(imageData);
                event.setImageContentType("image/jpeg");
            }
        } catch (IOException e) {
            LOGGER.warn("Could not load image from {}: {}", filePath, e.getMessage());
        }
    }
}
