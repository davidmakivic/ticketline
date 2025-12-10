package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService {
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
        Event entity = new Event(title, description, category, durationMinutes);

        if (image != null && !image.isEmpty()) {
            entity.setImageData(image.getBytes());
            entity.setImageContentType(image.getContentType());
        }

        Event saved = eventRepository.save(entity);
        return eventMapper.eventToEventDto(saved);
    }

    @Override
    public EventDto update(Long id, String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException {
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
        return eventMapper.eventToEventDto(saved);
    }


    @Override
    public EventDto findById(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event not found with id " + id));

        return eventMapper.eventToEventDto(event);
    }

    @Override
    public ResponseEntity<byte[]> getEventImage(Long id) {
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
        return eventMapper.eventToEventDto(eventRepository.findByAnyTitle(title));
    }

    @Override
    public List<EventDto> findAll() {
        return eventMapper.eventToEventDto(eventRepository.findAll());
    }

    @Override
    public void addArtist(Long eventId, Long artistId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        event.getArtists().add(artist);
        eventRepository.save(event);
    }

    @Override
    public void removeArtist(Long eventId, Long artistId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        event.getArtists().remove(artist);
        eventRepository.save(event);
    }

    @Override
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }
}
