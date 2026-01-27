package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Service interface for managing events.
 * Provides operations for creating, updating, retrieving, and deleting events,
 * as well as managing associated artists and retrieving autocomplete/top10 data.
 */
public interface EventService {

    /**
     * Creates a new event with the provided details and optional image.
     *
     * @param title the event title (required)
     * @param description the event description (required)
     * @param category the event category/type (required)
     * @param durationMinutes the event duration in minutes (required)
     * @param image the event image file (optional)
     * @return the created {@link EventDto} with all associated performances
     * @throws IOException if image processing fails
     */
    EventDto create(String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException;

    /**
     * Updates an existing event with new details and optional image.
     * Existing performances remain unaffected.
     *
     * @param id the event ID (required)
     * @param title the new event title (required)
     * @param description the new event description (required)
     * @param category the new event category/type (required)
     * @param durationMinutes the new event duration in minutes (required)
     * @param image the new event image file (optional, null to keep existing)
     * @return the updated {@link EventDto} with all associated performances
     * @throws IOException if image processing fails
     * @throws NotFoundException if event with given ID does not exist
     */
    EventDto update(Long id, String title, String description, EventType category, Integer durationMinutes, MultipartFile image) throws IOException;

    /**
     * Retrieves detailed information about a specific event including all performances.
     *
     * @param id the event ID (required)
     * @return the {@link EventDto} with all associated performances
     * @throws NotFoundException if event with given ID does not exist
     */
    EventDto findById(Long id);

    /**
     * Searches for events by title using case-insensitive partial matching.
     *
     * @param title the title to search for (required)
     * @return list of {@link EventDto} matching the title
     */
    List<EventDto> findByAnyTitle(String title);

    /**
     * Retrieves a paginated list of all events.
     *
     * @param page the page number (0-indexed)
     * @param size the number of items per page
     * @return a {@link Page} of {@link EventDto} sorted by ID in ascending order
     */
    Page<EventDto> findAll(int page, int size);

    /**
     * Searches for events using advanced filter criteria with pagination.
     * All filter parameters are optional and can be combined.
     *
     * @param title filter by event title (case-insensitive, partial match, optional)
     * @param artist filter by artist name (firstName, lastName, or stageName, optional)
     * @param location filter by venue name, city, or country (optional)
     * @param eventType filter by event category (CONCERT, FESTIVAL, MUSICAL, optional)
     * @param startDate filter by start date in ISO format (optional)
     * @param durationMinutes filter by event duration in minutes (optional)
     * @param page the page number (0-indexed)
     * @param size the number of items per page
     * @return a {@link Page} of {@link EventDto} matching the filters
     */
    Page<EventDto> findByAdvancedFilters(String title, String artist, String location,
                                         EventType eventType, Date startDate, Integer durationMinutes,
                                         int page, int size);

    /**
     * Adds an artist to an event.
     *
     * @param eventId the event ID (required)
     * @param artistId the artist ID to add (required)
     * @throws NotFoundException if event or artist with given ID does not exist
     */
    void addArtist(Long eventId, Long artistId);

    /**
     * Removes an artist from an event.
     *
     * @param eventId the event ID (required)
     * @param artistId the artist ID to remove (required)
     * @throws NotFoundException if event or artist with given ID does not exist
     */
    void removeArtist(Long eventId, Long artistId);

    /**
     * Deletes an event permanently along with all associated performances and tickets.
     *
     * @param id the event ID (required)
     * @throws NotFoundException if event with given ID does not exist
     */
    void delete(Long id);

    /**
     * Retrieves autocomplete suggestions for events by title.
     * Returns a limited list of events matching the provided title.
     *
     * @param title the title to search for (required)
     * @param limit the maximum number of results to return (required)
     * @return a list of {@link EventAutocompleteDto} matching the title
     */
    List<EventAutocompleteDto> findEventAutocomplete(String title, int limit);

    /**
     * Retrieves the top 10 most popular events of the current month.
     * Optionally filtered by event category.
     *
     * @param type the event category to filter by (CONCERT, FESTIVAL, MUSICAL, optional, null for all)
     * @return a list of top 10 {@link EventTop10Dto} for the current month
     */
    List<EventTop10Dto> getTop10ForCurrentMonth(EventType type);

    ResponseEntity<StreamingResponseBody> streamEventImage(Long id);
}
