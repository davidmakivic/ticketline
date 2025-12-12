package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
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

        Event e2 = new Event("König der Löwen",
            "Ein bewegendes Musical rund um Simba und sein Schicksal.",
            EventType.MUSICAL,
            150);

        Event e3 = new Event("Rock am Ring – Live Night",
            "Ein energiegeladenes Konzert mit international bekannten Rockbands.",
            EventType.CONCERT,
            180);

        Event e4 = new Event("Vienna Jazz Classics",
            "Ein Jazzkonzert mit klassischen und modernen Jazz-Interpretationen.",
            EventType.CONCERT,
            120);

        Event e5 = new Event("Sommer Festival 2025",
            "Ein großes Outdoor-Festival mit verschiedenen Künstlern und Acts.",
            EventType.FESTIVAL,
            300);

        Event e6 = new Event("Elektro Beats Festival",
            "Ein Festival für elektronische Musik, DJs und beeindruckende Lichtshows.",
            EventType.FESTIVAL,
            360);

        Event e7 = new Event("Symphonic Rock Night",
            "Ein außergewöhnliches Konzert, das Rock und Orchester kombiniert.",
            EventType.CONCERT,
            140);

        Event e8 = new Event("Mamma Mia! – Das Musical",
            "Das beliebte Musical basierend auf den größten Hits von ABBA.",
            EventType.MUSICAL,
            135);

        Event e9 = new Event("Pop Legends Live",
            "Ein Popkonzert der größten Chartstürmer des Jahres.",
            EventType.CONCERT,
            110);

        Event e10 = new Event("Indie Summer Festival",
            "Ein Festival mit bekannten Indie-Bands und Newcomern.",
            EventType.FESTIVAL,
            280);

        eventRepository.saveAll(List.of(
            e1, e2, e3, e4, e5, e6, e7, e8, e9, e10
        ));

        LOGGER.debug("10 demo events generated successfully");
    }
}
