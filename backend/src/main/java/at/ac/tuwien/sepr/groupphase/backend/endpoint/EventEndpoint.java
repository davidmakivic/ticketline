package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService eventService;

    public EventEndpoint(EventService eventService) {
        this.eventService = eventService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new event",
        description = "Creates a new event with title, description, category, duration and optional image. "
            + "Automatically prepares the event for performances to be added. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public EventDto create(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("category") EventType category,
        @RequestParam("durationMinutes") Integer durationMinutes,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        LOGGER.info("Creating new event: title={}, category={}, duration={}min, imagePresent={}",
            title, category, durationMinutes, image != null && !image.isEmpty());
        return eventService.create(title, description, category, durationMinutes, image);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update an existing event",
        description = "Updates event details including title, description, category, duration and optional image. "
            + "Existing performances remain unaffected."
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public EventDto update(
        @PathVariable Long id,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("category") EventType category,
        @RequestParam("durationMinutes") Integer durationMinutes,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        LOGGER.info("Updating event id={}: title={}, category={}, duration={}min, imagePresent={}",
            id, title, category, durationMinutes, image != null && !image.isEmpty());
        return eventService.update(id, title, description, category, durationMinutes, image);
    }

    @PermitAll
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get detailed information about a specific event",
        description = "Returns comprehensive details about an event including all associated performances. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public EventDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching event with id={}", id);
        return eventService.findById(id);
    }

    @PermitAll
    @GetMapping("/{id}/image")
    @Operation(
        summary = "Get image for a specific event",
        description = "Returns the image associated with an event in its original format. "
            + "Returns 204 No Content if no image is available. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No image available for this event"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<StreamingResponseBody> getEventImage(@PathVariable Long id) {
        LOGGER.info("Fetching event image for id={}", id);
        return eventService.streamEventImage(id);
    }

    @PermitAll
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get paginated list of all events",
        description = "Returns a paginated list of all events sorted by ID in ascending order. "
            + "Publicly accessible.",
        parameters = {
            @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public Page<EventDto> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Fetching events page={}, size={}", page, size);
        return eventService.findAll(page, size);
    }

    @PermitAll
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Search events by advanced filters with pagination",
        description = "Returns a paginated list of events filtered by optional criteria."
            + "All filter parameters are optional and can be combined. "
            + "Publicly accessible.",
        parameters = {
            @Parameter(name = "title", description = "Filter by event title (case-insensitive, partial match)"),
            @Parameter(name = "artist", description = "Filter by artist name (firstName, lastName, or stageName)"),
            @Parameter(name = "location", description = "Filter by venue name, city, or country"),
            @Parameter(name = "eventType", description = "Filter by event category (CONCERT, FESTIVAL, MUSICAL)"),
            @Parameter(name = "startDate", description = "Filter by start date (ISO date format: yyyy-MM-dd)"),
            @Parameter(name = "durationMinutes", description = "Filter by event duration in minutes"),
            @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered events"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public Page<EventDto> searchByAdvancedFilters(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String artist,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) EventType eventType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
        @RequestParam(required = false) Integer durationMinutes,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Searching events with filters: title={}, artist={}, location={}, eventType={}, startDate={}, durationMinutes={}, page={}, size={}",
            title, artist, location, eventType, startDate, durationMinutes, page, size);
        return eventService.findByAdvancedFilters(title, artist, location, eventType, startDate, durationMinutes, page, size);
    }

    @PermitAll
    @GetMapping("/autocomplete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get events by title for autocomplete",
        description = "Returns a limited list of events matching the provided title for autocomplete functionality. "
            + "Publicly accessible.",
        parameters = {
            @Parameter(name = "title", description = "Event title to search for (required)", example = "Symphony"),
            @Parameter(name = "limit", description = "Maximum number of results to return (required)", example = "10")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved autocomplete suggestions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public List<EventAutocompleteDto> getAutocompleteByTitle(
        @RequestParam("title") String title,
        @RequestParam("limit") int limit) {
        LOGGER.info("Fetching autocomplete for title={}", title);
        return eventService.findEventAutocomplete(title, limit);
    }

    @PermitAll
    @GetMapping("/top10")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get top 10 events by category for current month",
        description = "Returns the top 10 most popular events of the current month, optionally filtered by category. "
            + "If no category is specified, returns top 10 across all categories. "
            + "Publicly accessible.",
        parameters = {
            @Parameter(name = "eventType", description = "Filter by event category (CONCERT, FESTIVAL, MUSICAL) - optional")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top 10 events"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public List<EventTop10Dto> getTop10ByCategory(
        @RequestParam(value = "eventType", required = false) EventType eventType) {
        LOGGER.info("Fetching top 10 events for eventType={}", eventType);
        return eventService.getTop10ForCurrentMonth(eventType);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete an event",
        description = "Permanently deletes an event and all associated performances and tickets. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOGGER.info("Deleting event with id={}", id);
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

