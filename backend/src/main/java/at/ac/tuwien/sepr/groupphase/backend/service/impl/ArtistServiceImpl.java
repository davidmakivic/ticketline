package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistMapper artistMapper, EventRepository eventRepository, EventMapper eventMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public ArtistDto create(String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException {
        Artist entity = new Artist(firstName, lastName, stageName, artistType);

        if (image != null && !image.isEmpty()) {
            entity.setImageData(image.getBytes());
            entity.setImageContentType(image.getContentType());
        }

        Artist savedArtist = artistRepository.save(entity);
        return artistMapper.artistToArtistDto(savedArtist);
    }

    @Override
    public ArtistDto update(Long id, String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException {
        Artist existingArtist = artistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Artist not found with id " + id));

        existingArtist.setFirstName(firstName);
        existingArtist.setLastName(lastName);
        existingArtist.setStageName(stageName);
        existingArtist.setArtistType(artistType);

        if (image != null && !image.isEmpty()) {
            existingArtist.setImageData(image.getBytes());
            existingArtist.setImageContentType(image.getContentType());
        }
        Artist updatedArtist = artistRepository.save(existingArtist);
        return artistMapper.artistToArtistDto(updatedArtist);
    }

    @Override
    public ArtistDto findById(Long id) {
        Artist artist = artistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Artist not found with id " + id));

        return artistMapper.artistToArtistDto(artist);
    }

    @Override
    public ResponseEntity<byte[]> getArtistImage(Long id) {
        Artist artist = artistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + id));

        if (artist.getImageData() == null || artist.getImageData().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(artist.getImageContentType()))
            .body(artist.getImageData());
    }



    @Override
    public List<ArtistDto> findAll() {
        return artistMapper.artistToArtistDto(artistRepository.findAll());
    }

    @Override
    public void addEvent(Long artistId, Long eventId) {
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        artist.getEvents().add(event);
        artistRepository.save(artist);
    }

    @Override
    public void deleteEvent(Long artistId, Long eventId) {
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        artist.getEvents().remove(event);
        artistRepository.save(artist);
    }

    @Override
    public List<EventDto> findEventsByArtistId(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + artistId));

        return artist.getEvents()
            .stream()
            .map(eventMapper::eventToEventDto)
            .toList();
    }

    @Override
    public List<ArtistDto> findByName(String name) {
        var artists = artistRepository.findByAnyName(name);
        return getArtistListDtoStream(artists).toList();
    }

    private Stream<ArtistDto> getArtistListDtoStream(List<Artist> artists) {
        return artists.stream()
            .map(artistMapper::artistToArtistDto);
    }

    @Override
    public void delete(Long id) {
        artistRepository.deleteById(id);
    }
}
