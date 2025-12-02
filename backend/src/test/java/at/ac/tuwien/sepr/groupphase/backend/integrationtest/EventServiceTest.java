package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateEvent() {
        Event e = new Event();
        e.setTitle("Test Event");
        e.setDescription("Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(30);

        Event saved = eventService.create(e);

        assertThat(saved.getTitle()).isEqualTo("Test Event");
    }

    @Transactional
    @Test
    void testUpdateEvent() {
        Event e = new Event();
        e.setTitle("Original");
        e.setDescription("Original Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(30);

        Event saved = eventService.create(e);
        Long id = saved.getId();

        Event updated = new Event();
        updated.setTitle("Updated Title");
        updated.setDescription("Updated Desc");
        updated.setCategory(EventType.MUSICAL);
        updated.setDurationMinutes(60);

        Event result = eventService.update(id, updated);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getCategory()).isEqualTo(EventType.MUSICAL);
    }

    @Transactional
    @Test
    void testUpdateEventNotFound() {
        Event updated = new Event();
        updated.setTitle("Doesn't matter");

        assertThatThrownBy(() -> eventService.update(999L, updated))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAll() {
        Event e1 = new Event();
        e1.setTitle("A");
        e1.setDescription("Desc");
        e1.setCategory(EventType.FESTIVAL);
        e1.setDurationMinutes(10);

        Event e2 = new Event();
        e2.setTitle("B");
        e2.setCategory(EventType.MUSICAL);
        e2.setDurationMinutes(20);

        eventService.create(e1);
        eventService.create(e2);

        List<Event> result = eventService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testDeleteEvent() {
        Event e = new Event();
        e.setTitle("To be deleted");
        e.setDescription("Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(45);

        Event saved = eventService.create(e);
        Long id = saved.getId();

        assertThat(eventService.findById(id)).isNotNull();

        eventService.delete(id);

        assertThatThrownBy(() -> eventService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }
}
