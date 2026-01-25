package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * Service interface for managing artists and their associations with events.
 * Provides operations for CRUD operations, image handling, and search functionality.
 */

public interface ArtistService {

    /**
     * Creates a new artist with the provided details and optional image.
     *
     * @param firstName the artist's first name
     * @param lastName the artist's last name
     * @param stageName the artist's stage name
     * @param artistType the type/category of the artist
     * @param image optional image file for the artist
     * @return the created artist as DTO
     * @throws IOException if image processing fails
     */
    ArtistDto create(String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException;

    /**
     * Updates an existing artist with new details and optional image.
     *
     * @param id the ID of the artist to update
     * @param firstName the updated first name
     * @param lastName the updated last name
     * @param stageName the updated stage name
     * @param artistType the updated artist type
     * @param image optional new image file
     * @return the updated artist as DTO
     * @throws IOException if image processing fails
     * @throws NotFoundException if artist with given ID doesn't exist
     */
    ArtistDto update(Long id, String firstName, String lastName, String stageName, ArtistType artistType, MultipartFile image) throws IOException;

    /**
     * Finds an artist by their unique identifier.
     *
     * @param id the artist's ID
     * @return the artist as DTO
     * @throws NotFoundException if artist with given ID doesn't exist
     */
    ArtistDto findById(Long id);

    /**
     * Retrieves the image associated with an artist.
     *
     * @param id the artist's ID
     * @return ResponseEntity containing the image bytes with appropriate content type,
     *         or 204 No Content if no image exists
     * @throws NotFoundException if artist with given ID doesn't exist
     */
    ResponseEntity<byte[]> getArtistImage(Long id);

    /**
     * Retrieves all artists in the system.
     *
     * @return list of all artists as DTOs
     */
    List<ArtistDto> findAll();

    /**
     * Associates an event with an artist.
     *
     * @param artistId the ID of the artist
     * @param eventId the ID of the event to add
     * @throws NotFoundException if artist or event doesn't exist
     */
    void addEvent(Long artistId, Long eventId);

    /**
     * Removes the association between an artist and an event.
     *
     * @param artistId the ID of the artist
     * @param eventId the ID of the event to remove
     * @throws NotFoundException if artist or event doesn't exist
     */
    void deleteEvent(Long artistId, Long eventId);

    /**
     * Retrieves all events associated with a specific artist.
     *
     * @param artistId the ID of the artist
     * @return list of events as DTOs
     * @throws NotFoundException if artist with given ID doesn't exist
     */
    List<EventDto> findEventsByArtistId(Long artistId);

    /**
     * Searches for artists by name (firstName, lastName, or stageName).
     *
     * @param name the name to search for
     * @return list of matching artists as DTOs
     */
    List<ArtistDto> findByName(String name);

    /**
     * Deletes an artist and removes all associations with events.
     *
     * @param id the ID of the artist to delete
     * @throws NotFoundException if artist with given ID doesn't exist
     */
    void delete(Long id);

    /**
     * Finds artists matching the given name for autocomplete functionality.
     *
     * @param name the name to search for (partial match)
     * @param maxAmount the maximum number of results to return
     * @return list of matching artists as autocomplete DTOs
     */
    List<ArtistAutocompleteDto> findArtistAutocomplete(String name, int maxAmount);
}
