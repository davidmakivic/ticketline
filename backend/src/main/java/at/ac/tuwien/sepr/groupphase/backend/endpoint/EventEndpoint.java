package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService eventService;

    public EventEndpoint(EventService eventService) {
        this.eventService = eventService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EventDto create(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("category") EventType category,
        @RequestParam("durationMinutes") Integer durationMinutes,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {


        LOGGER.info("Request to create new event: {}", title);
        LOGGER.debug("Create event payload: title={}, descriptionLength={}, category={}, duration={}, imagePresent={}",
            title, description != null ? description.length() : 0, category, durationMinutes, image != null);
        return eventService.create(title, description, category, durationMinutes, image);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EventDto update(
        @PathVariable Long id,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("category") EventType category,
        @RequestParam("durationMinutes") Integer durationMinutes,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        LOGGER.info("Request to update event id={}", id);
        LOGGER.debug("Update event payload: title={}, descriptionLength={}, category={}, duration={}, imagePresent={}",
            title, description != null ? description.length() : 0, category, durationMinutes, image != null);
        return eventService.update(id, title, description, category, durationMinutes, image);
    }

    @PermitAll
    @GetMapping("/{id}")
    public EventDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching event with id={}", id);
        return eventService.findById(id);
    }

    @PermitAll
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long id) {
        LOGGER.info("Fetching event image for id={}", id);
        return eventService.getEventImage(id);
    }

    @PermitAll
    @GetMapping("/query")
    public List<EventDto> searchByAdvancedFilters(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String artist,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) EventType eventType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
        @RequestParam(required = false) Integer durationMinutes) {
        LOGGER.info("Searching events with advanced filters");
        return eventService.findByAdvancedFilters(title, artist, location, eventType, startDate, durationMinutes);
    }


    @PermitAll
    @GetMapping
    public List<EventDto> getAll() {
        LOGGER.info("Fetching all events");
        LOGGER.debug("Event list requested");
        return eventService.findAll()
            .stream()
            .toList();
    }

    @PermitAll
    @GetMapping("/autocomplete")
    public List<EventAutocompleteDto> getAutocompleteByTitle(
        @RequestParam("title") String title,
        @RequestParam("limit") int limit) {
        LOGGER.info("Fetching artists by name={}", title);
        return eventService.findEventAutocomplete(title, limit);
    }

    @PermitAll
    @GetMapping("/top10")
    public List<EventTop10Dto> getTop10ByCategory(@RequestParam(value = "eventType", required = false) EventType eventType) {
        return eventService.getTop10ForCurrentMonth(eventType);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        LOGGER.info("Deleting event with id={}", id);
        eventService.delete(id);
    }
}
