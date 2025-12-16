package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArtistService {
    ArtistDto create(String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException;

    ArtistDto update(Long id, String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException;

    ArtistDto findById(Long id);

    ResponseEntity<byte[]> getArtistImage(Long id);

    List<ArtistDto> findAll();

    void addEvent(Long artistId, Long eventId);

    void deleteEvent(Long artistId, Long eventId);

    List<EventDto> findEventsByArtistId(Long artistId);

    List<ArtistDto> findByName(String name);

    void delete(Long id);

    List<ArtistAutocompleteDto> findArtistAutocomplete(String name, int maxAmount);
}
