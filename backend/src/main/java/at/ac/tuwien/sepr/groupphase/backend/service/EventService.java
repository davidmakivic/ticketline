package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    EventDto create(String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException;

    EventDto update(Long id, String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException;

    EventDto findById(Long id);

    List<EventDto> findByAnyTitle(String title);

    List<EventDto> findAll();

    void addArtist(Long eventId, Long artistId);

    void removeArtist(Long eventId, Long artistId);

    void delete(Long id);
}
