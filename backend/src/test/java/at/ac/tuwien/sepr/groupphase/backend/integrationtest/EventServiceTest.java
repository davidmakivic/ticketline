package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
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

    @Autowired
    private ArtistService artistService;

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateEvent() {
        EventDto e = new EventDto();
        e.setTitle("Test Event");
        e.setDescription("Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(30);

        EventDto saved = eventService.create(e);

        assertThat(saved.getTitle()).isEqualTo("Test Event");
    }

    @Transactional
    @Test
    void testUpdateEvent() {
        EventDto e = new EventDto();
        e.setTitle("Original");
        e.setDescription("Original Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(30);

        EventDto saved = eventService.create(e);
        Long id = saved.getId();

        EventDto updated = new EventDto();
        updated.setTitle("Updated Title");
        updated.setDescription("Updated Desc");
        updated.setCategory(EventType.MUSICAL);
        updated.setDurationMinutes(60);

        EventDto result = eventService.update(id, updated);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getCategory()).isEqualTo(EventType.MUSICAL);
    }

    @Transactional
    @Test
    void testUpdateEventNotFound() {
        EventDto updated = new EventDto();
        updated.setTitle("Doesn't matter");

        assertThatThrownBy(() -> eventService.update(999L, updated))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAll() {
        EventDto e1 = new EventDto();
        e1.setTitle("A");
        e1.setDescription("Desc");
        e1.setCategory(EventType.FESTIVAL);
        e1.setDurationMinutes(10);

        EventDto e2 = new EventDto();
        e2.setTitle("B");
        e2.setCategory(EventType.MUSICAL);
        e2.setDurationMinutes(20);

        eventService.create(e1);
        eventService.create(e2);

        List<EventDto> result = eventService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testDeleteEvent() {
        EventDto e = new EventDto();
        e.setTitle("To be deleted");
        e.setDescription("Desc");
        e.setCategory(EventType.CONCERT);
        e.setDurationMinutes(45);

        EventDto saved = eventService.create(e);
        Long id = saved.getId();

        assertThat(eventService.findById(id)).isNotNull();

        eventService.delete(id);

        assertThatThrownBy(() -> eventService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testAddArtistToEvent() {
        EventDto event = new EventDto();
        event.setTitle("My Event");
        event.setDescription("Desc");
        event.setCategory(EventType.CONCERT);
        event.setDurationMinutes(60);

        EventDto savedEvent = eventService.create(event);

        ArtistDto artist = new ArtistDto();
        artist.setFirstName("John");
        artist.setLastName("Smith");
        artist.setStageName("JS");
        artist.setArtistType(ArtistType.SOLO);

        ArtistDto savedArtist = artistService.create(artist);

        eventService.addArtist(savedEvent.getId(), savedArtist.getId());

        EventDto updated = eventService.findById(savedEvent.getId());

        assertThat(updated.getArtists()).hasSize(1);
    }

    @Transactional
    @Test
    void testRemoveArtistFromEvent() {
        EventDto event = new EventDto();
        event.setTitle("My Event");
        event.setDescription("Desc");
        event.setCategory(EventType.CONCERT);
        event.setDurationMinutes(60);

        EventDto savedEvent = eventService.create(event);

        ArtistDto artist = new ArtistDto();
        artist.setFirstName("Anna");
        artist.setLastName("Jones");
        artist.setStageName("AJ");
        artist.setArtistType(ArtistType.SOLO);

        ArtistDto savedArtist = artistService.create(artist);

        eventService.addArtist(savedEvent.getId(), savedArtist.getId());

        eventService.removeArtist(savedEvent.getId(), savedArtist.getId());

        EventDto updated = eventService.findById(savedEvent.getId());

        assertThat(updated.getArtists()).isEmpty();
    }
}
