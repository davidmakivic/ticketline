package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ArtistService {
    ArtistDto create(ArtistDto artist);

    ArtistDto update(Long id, ArtistDto artist);

    ArtistDto findById(Long id);

    ResponseEntity<byte[]> getArtistImage(Long id);

    List<ArtistDto> findAll();

    void addEvent(Long artistId, Long eventId);

    void deleteEvent(Long artistId, Long eventId);

    List<ArtistDto> findByName(String name);

    void delete(Long id);
}
