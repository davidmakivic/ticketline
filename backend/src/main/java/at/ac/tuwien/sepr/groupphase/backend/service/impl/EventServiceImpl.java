package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import org.springframework.stereotype.Service;

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
    public EventDto create(EventDto eventDto) {
        Event entity = eventMapper.eventDtoToEvent(eventDto);
        Event saved = eventRepository.save(entity);
        return eventMapper.eventToEventDto(saved);
    }

    @Override
    public EventDto update(Long id, EventDto updatedEventDto) {
        Event existing = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event not found: " + id));

        // MapStruct partial update
        eventMapper.updateEntityFromDto(updatedEventDto, existing);

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
