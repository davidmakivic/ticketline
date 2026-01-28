package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

public interface ArtistService {
    ArtistDto create(String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException;

    ArtistDto update(Long id, String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException;

    ArtistDto findById(Long id);

    Page<ArtistDto> findAll(int page, int size);


    void addEvent(Long artistId, Long eventId);

    void deleteEvent(Long artistId, Long eventId);

    List<EventDto> findEventsByArtistId(Long artistId);

    List<ArtistDto> findByName(String name);

    void delete(Long id);

    List<ArtistAutocompleteDto> findArtistAutocomplete(String name, int maxAmount);

    ResponseEntity<StreamingResponseBody> streamArtistImage(Long id);
}
