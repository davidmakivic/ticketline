package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testCreateEventWithoutImage() throws IOException {
        EventDto created = eventService.create(
            "Test Event",
            "Test Description",
            EventType.CONCERT,
            120,
            null
        );

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Event", created.getTitle());
        assertEquals("Test Description", created.getDescription());
        assertEquals(EventType.CONCERT, created.getCategory());
        assertEquals(120, created.getDurationMinutes());
    }

    @Test
    void testCreateEventWithImage() throws IOException {
        MultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        EventDto created = eventService.create(
            "Test Event",
            "Test Description",
            EventType.FESTIVAL,
            90,
            image
        );

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Event", created.getTitle());
        assertEquals(EventType.FESTIVAL, created.getCategory());

        Event eventFromDb = eventRepository.findById(created.getId()).orElse(null);
        assertNotNull(eventFromDb);
        assertNotNull(eventFromDb.getImageData());
        assertEquals("image/jpeg", eventFromDb.getImageContentType());
    }

    @Test
    void testUpdateEventWithoutImage() throws IOException {
        MultipartFile initialImage = new MockMultipartFile(
            "image",
            "initial.jpg",
            "image/jpeg",
            "initial content".getBytes()
        );

        EventDto created = eventService.create(
            "Original Title",
            "Original Description",
            EventType.CONCERT,
            60,
            initialImage
        );

        EventDto updated = eventService.update(
            created.getId(),
            "Updated Title",
            "Updated Description",
            EventType.MUSICAL,
            120,
            null
        );

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(EventType.MUSICAL, updated.getCategory());
        assertEquals(120, updated.getDurationMinutes());

        Event eventFromDb = eventRepository.findById(updated.getId()).orElse(null);
        assertNotNull(eventFromDb);
        assertNotNull(eventFromDb.getImageData());
    }

    @Test
    void testUpdateEventWithNewImage() throws IOException {
        EventDto created = eventService.create(
            "Test Event",
            "Description",
            EventType.CONCERT,
            60,
            null
        );

        MultipartFile newImage = new MockMultipartFile(
            "image",
            "new.png",
            "image/png",
            "new image content".getBytes()
        );

        EventDto updated = eventService.update(
            created.getId(),
            "Test Event",
            "Description",
            EventType.CONCERT,
            60,
            newImage
        );

        Event eventFromDb = eventRepository.findById(updated.getId()).orElse(null);
        assertNotNull(eventFromDb);
        assertNotNull(eventFromDb.getImageData());
        assertEquals("image/png", eventFromDb.getImageContentType());
    }

    @Test
    void testFindById() throws IOException {
        EventDto created = eventService.create(
            "Test Event",
            "Description",
            EventType.CONCERT,
            90,
            null
        );

        EventDto found = eventService.findById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Test Event", found.getTitle());
    }

    @Test
    void testFindByIdNotFound() {
        assertThrows(NotFoundException.class, () -> eventService.findById(999L));
    }

    @Test
    void testFindByAnyTitle() throws IOException {
        eventService.create("Concert Event", "Description", EventType.CONCERT, 60, null);
        eventService.create("Concert Festival", "Description", EventType.FESTIVAL, 120, null);
        eventService.create("Theater Show", "Description", EventType.MUSICAL, 90, null);

        var results = eventService.findByAnyTitle("Concert");

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(e -> e.getTitle().contains("Concert")));
    }

    @Test
    void testFindAll() throws IOException {
        eventService.create("Event 1", "Description", EventType.CONCERT, 60, null);
        eventService.create("Event 2", "Description", EventType.FESTIVAL, 120, null);

        var all = eventService.findAll(0,10);

        assertEquals(2, all.getTotalElements());
    }

    @Test
    void testDelete() throws IOException {
        EventDto created = eventService.create(
            "Event to Delete",
            "Description",
            EventType.CONCERT,
            60,
            null
        );

        eventService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> eventService.findById(created.getId()));
    }

    @Test
    void testGetEventImage() throws IOException {
        MultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        EventDto created = eventService.create(
            "Test Event",
            "Description",
            EventType.CONCERT,
            60,
            image
        );

        var response = eventService.getEventImage(created.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetEventImageNotFound() {
        assertThrows(NotFoundException.class, () -> eventService.getEventImage(999L));
    }

    @Test
    void testGetEventImageNoImageData() throws IOException {
        EventDto created = eventService.create(
            "Test Event",
            "Description",
            EventType.CONCERT,
            60,
            null
        );

        var response = eventService.getEventImage(created.getId());

        assertEquals(204, response.getStatusCode().value());
    }
}
