package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventEndpoint {

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

        return eventService.update(id, title, description, category, durationMinutes, image);
    }

    @PermitAll
    @GetMapping("/{id}")
    public EventDto getById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PermitAll
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long id) {
        return eventService.getEventImage(id);
    }

    @PermitAll
    @GetMapping("/query")
    public List<EventDto> searchByTitle(@RequestParam String title) {
        return eventService.findByAnyTitle(title);
    }

    @PermitAll
    @GetMapping
    public List<EventDto> getAll() {
        return eventService.findAll()
            .stream()
            .toList();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }
}
