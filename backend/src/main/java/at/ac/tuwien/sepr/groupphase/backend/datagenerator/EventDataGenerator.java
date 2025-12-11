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

@Profile("generateData")
@Component
public class EventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_EVENTS_TO_GENERATE = 5;
    private static final String TEST_EVENT_TITLE = "Event Title";
    private static final String TEST_EVENT_DESCRIPTION = "Event Description";
    private static final int TEST_EVENT_DURATION_MINUTES = 60;

    private final EventRepository eventRepository;

    public EventDataGenerator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void generateEventData() {
        if (eventRepository.findAll().size() > 0) {
            LOGGER.debug("Event already generated");
        } else {
            LOGGER.debug("generating {} message entries", NUMBER_OF_EVENTS_TO_GENERATE);
            for (int i = 0; i <= NUMBER_OF_EVENTS_TO_GENERATE; i++) {
                Event event = new Event();
                event.setTitle(TEST_EVENT_TITLE);
                event.setDescription(TEST_EVENT_DESCRIPTION);
                event.setCategory(EventType.MUSICAL);
                event.setDurationMinutes(TEST_EVENT_DURATION_MINUTES);
                LOGGER.debug("saving event {}", event);
                eventRepository.save(event);
            }
        }
    }
}
