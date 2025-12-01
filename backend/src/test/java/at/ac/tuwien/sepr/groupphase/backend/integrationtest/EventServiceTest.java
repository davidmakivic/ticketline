package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Transactional
    @Test
    void testCreateEvent() {
        Event e = new Event();
        e.setId(1);
        e.setTitle("Test Event");
        e.setDescription("Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(30);

        Event saved = eventService.create(e);

        assertThat(saved.getTitle()).isEqualTo("Test Event");
    }

    @Transactional
    @Test
    void testFindAll() {
        Event e1 = new Event();
        e1.setId(2);
        e1.setTitle("A");
        e1.setDescription("Desc");
        e1.setCategory(EventType.FESTIVAL);
        e1.setDurationMinutes(10);

        Event e2 = new Event();
        e2.setId(3);
        e2.setTitle("B");
        e2.setCategory(EventType.MUSICAL);
        e2.setDurationMinutes(20);

        eventService.create(e1);
        eventService.create(e2);

        List<Event> result = eventService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }
}
