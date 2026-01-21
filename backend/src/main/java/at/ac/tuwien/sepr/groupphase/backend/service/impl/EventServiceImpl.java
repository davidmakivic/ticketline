package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.specification.EventSpecifications;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, ArtistRepository artistRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDto create(String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException {
        LOGGER.info("Creating event '{}'", title);
        LOGGER.debug("Payload: category={}, duration={}, imagePresent={}", category, durationMinutes, image != null);
        Event entity = new Event(title, description, category, durationMinutes);

        if (image != null && !image.isEmpty()) {
            entity.setImageData(image.getBytes());
            entity.setImageContentType(image.getContentType());
        }

        Event saved = eventRepository.save(entity);
        return eventMapper.eventToEventDtoWithPerformances(saved);
    }

    @Override
    public EventDto update(Long id, String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException {
        LOGGER.info("Updating event with id={}", id);
        LOGGER.debug("Payload: title={}, description={}, category={}, duration={}, imagePresent={}",
            title, description, category, durationMinutes, image != null);
        Event existing = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event not found: " + id));

        existing.setTitle(title);
        existing.setDescription(description);
        existing.setCategory(category);
        existing.setDurationMinutes(durationMinutes);

        if (image != null && !image.isEmpty()) {
            existing.setImageData(image.getBytes());
            existing.setImageContentType(image.getContentType());
        }

        Event saved = eventRepository.save(existing);
        return eventMapper.eventToEventDtoWithPerformances(saved);
    }


    @Override
    public EventDto findById(Long id) {
        LOGGER.info("Fetching event with id={}", id);
        Event event = eventRepository.findByIdWithPerformances(id)
            .orElseThrow(() -> new NotFoundException("Event not found with id " + id));
        return eventMapper.eventToEventDtoWithPerformances(event);
    }

    @Override
    public ResponseEntity<byte[]> getEventImage(Long id) {
        LOGGER.info("Fetching event image for id={}", id);
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event not found: " + id));

        if (event.getImageData() == null || event.getImageData().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(event.getImageContentType()))
            .body(event.getImageData());
    }

    @Override
    public List<EventDto> findByAnyTitle(String title) {
        LOGGER.info("Searching events by title: {}", title);
        return eventMapper.eventToEventDtoList(eventRepository.findByAnyTitle(title));
    }

    @Override
    public Page<EventDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return eventRepository.findAllPaginated(pageable)
            .map(eventMapper::eventToEventDto);
    }

    @Override
    public Page<EventDto> findByAdvancedFilters(String title, String artist, String location,
                                                EventType eventType, Date startDate, Integer durationMinutes,
                                                int page, int size) {
        LOGGER.info("Searching events with filters: title={}, artist={}, location={}, eventType={}, startDate={}, duration={}",
            title, artist, location, eventType, startDate, durationMinutes);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Specification<Event> spec = Specification.allOf(
            EventSpecifications.hasTitle(title),
            EventSpecifications.hasArtist(artist),
            EventSpecifications.hasLocation(location),
            EventSpecifications.hasEventType(eventType),
            EventSpecifications.hasStartDate(startDate),
            EventSpecifications.hasDuration(durationMinutes),
            EventSpecifications.fetchPerformances()
        );

        return eventRepository.findAll(spec, pageable)
            .map(eventMapper::eventToEventDtoWithPerformances);
    }

    @Override
    public void addArtist(Long eventId, Long artistId) {
        LOGGER.info("Adding artist {} to event {}", artistId, eventId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        event.getArtists().add(artist);
        eventRepository.save(event);
    }

    @Override
    public void removeArtist(Long eventId, Long artistId) {
        LOGGER.info("Removing artist {} from event {}", artistId, eventId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        event.getArtists().remove(artist);
        eventRepository.save(event);
    }

    @Override
    public void delete(Long id) {
        LOGGER.info("Deleting event with id={}", id);
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventAutocompleteDto> findEventAutocomplete(String title, int limit) {
        return this.eventRepository.findEventAutocompleteDto(title, PageRequest.of(0, limit));
    }

    @Override
    public List<EventTop10Dto> getTop10ForCurrentMonth(EventType type) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        Pageable top10 = PageRequest.of(0, 10);
        return eventRepository.findTopEventsOfMonth(startOfMonth, endOfMonth, type, type == null, top10);
    }
}
