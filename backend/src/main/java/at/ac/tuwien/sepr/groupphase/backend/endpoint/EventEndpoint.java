package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventEndpoint {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventEndpoint(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public EventDto create(@RequestBody EventDto dto) {
        var saved = eventService.create(eventMapper.eventDtoToEvent(dto));
        return eventMapper.eventToEventDto(saved);
    }

    @PermitAll
    @GetMapping("/{id}")
    public EventDto getById(@PathVariable int id) {
        return eventMapper.eventToEventDto(eventService.findById(id));
    }

    @PermitAll
    @GetMapping
    public List<EventDto> getAll() {
        return eventService.findAll()
            .stream()
            .map(eventMapper::eventToEventDto)
            .toList();
    }
}
