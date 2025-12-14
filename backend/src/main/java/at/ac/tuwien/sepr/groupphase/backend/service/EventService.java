package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface EventService {
    EventDto create(String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException;

    EventDto update(Long id, String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException;

    EventDto findById(Long id);

    ResponseEntity<byte[]> getEventImage(Long id);

    List<EventDto> findByAnyTitle(String title);

    List<EventDto> findByAdvancedFilters(String title, String artist, String location,
                                         EventType eventType, Date startDate, Integer durationMinutes);

    List<EventDto> findAll();

    void addArtist(Long eventId, Long artistId);

    void removeArtist(Long eventId, Long artistId);

    void delete(Long id);

    List<EventAutocompleteDto> findEventAutocomplete(String title, int limit);

    List<EventTop10Dto> getTop10ForCurrentMonth(EventType type);
}
