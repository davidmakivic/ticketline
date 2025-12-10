package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    void testCreateEvent() throws Exception {
        EventDto saved = eventService.create("Test Event", "Desc", EventType.CONCERT, 30, null);

        assertThat(saved.getTitle()).isEqualTo("Test Event");
    }

    @Transactional
    @Test
    void testCreateEventWithImage() throws Exception {
        byte[] imageContent = "fake image content".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageContent);

        EventDto saved = eventService.create("Test Event", "Desc", EventType.CONCERT, 30, image);

        assertThat(saved.getTitle()).isEqualTo("Test Event");
        assertThat(saved.getImageData()).isEqualTo(imageContent);
        assertThat(saved.getImageContentType()).isEqualTo("image/jpeg");
    }

    @Transactional
    @Test
    void testUpdateEvent() throws IOException {
        EventDto saved = eventService.create("Original", "Original Desc", EventType.CONCERT, 30, null);
        Long id = saved.getId();

        byte[] imageContent = "updated image".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "updated.jpg", "image/jpeg", imageContent);

        EventDto result = eventService.update(id, "Updated Title", "Updated Desc", EventType.MUSICAL, 60, image);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getCategory()).isEqualTo(EventType.MUSICAL);
        assertThat(result.getDurationMinutes()).isEqualTo(60);
    }

    @Transactional
    @Test
    void testUpdateEventNotFound() {
        assertThatThrownBy(() -> eventService.update(999L, "Title", "Desc", EventType.CONCERT, 30, null))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAll() throws Exception {
        eventService.create("A", "Desc", EventType.FESTIVAL, 10, null);
        eventService.create("B", "Desc", EventType.MUSICAL, 20, null);

        List<EventDto> result = eventService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testDeleteEvent() throws Exception {
        EventDto saved = eventService.create("To be deleted", "Desc", EventType.CONCERT, 45, null);
        Long id = saved.getId();

        assertThat(eventService.findById(id)).isNotNull();

        eventService.delete(id);

        assertThatThrownBy(() -> eventService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testAddArtistToEvent() throws Exception {
        EventDto savedEvent = eventService.create("My Event", "Desc", EventType.CONCERT, 60, null);

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
    void testRemoveArtistFromEvent() throws Exception {
        EventDto savedEvent = eventService.create("My Event", "Desc", EventType.CONCERT, 60, null);

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
