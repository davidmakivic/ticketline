package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;

import java.util.List;

public interface EventService {
    EventDto create(EventDto event);

    EventDto update(Long id, EventDto event);

    EventDto findById(Long id);

    List<EventDto> findAll();

    void addArtist(Long eventId, Long artistId);

    void removeArtist(Long eventId, Long artistId);

    void delete(Long id);
}
