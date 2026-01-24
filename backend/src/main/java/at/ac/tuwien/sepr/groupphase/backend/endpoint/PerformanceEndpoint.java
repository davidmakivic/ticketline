package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/v1/performances")
@Tag(name = "Performances")
public class PerformanceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceService performanceService;

    public PerformanceEndpoint(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PermitAll
    @GetMapping
    @Operation(
        summary = "Get all performances with optional filters",
        description = "Returns a paginated list of performances. Can be filtered by title, artist, location, "
            + "event type, start date, and duration. If no filters are provided, returns all performances "
            + "sorted by start time. Publicly accessible.",
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
            @ApiResponse(responseCode = "200", description = "Successfully retrieved performances"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public Page<PerformanceDto> getAll(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String artist,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) EventType eventType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
        @RequestParam(required = false) Integer durationMinutes,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Fetching performances with filters: title={}, artist={}, location={}, eventType={}, startDate={}, durationMinutes={}, page={}, size={}",
            title, artist, location, eventType, startDate, durationMinutes, page, size);

        // Wenn keine Filter gesetzt sind, alle zurückgeben
        if (title == null && artist == null && location == null
            && eventType == null && startDate == null && durationMinutes == null) {
            LOGGER.debug("No filters applied, returning all performances");
            return performanceService.findAll(page, size);
        }

        return performanceService.findByAdvancedFilters(
            title, artist, location, eventType, startDate, durationMinutes, page, size);
    }


    @PermitAll
    @GetMapping("/{id}")
    @Operation(
        summary = "Get performance by ID",
        description = "Returns detailed information about a specific performance including event, hall, and venue details. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Performance found"),
            @ApiResponse(responseCode = "404", description = "Performance not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public PerformanceDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching performance with id={}", id);
        return performanceService.findById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(
        summary = "Create a new performance",
        description = "Creates a new performance and automatically generates tickets for all seats in the hall. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Performance created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Event or Hall not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public PerformanceDto create(@RequestBody PerformanceDto dto) {
        LOGGER.info("Creating new performance for event={}, hall={}", dto.getEventId(), dto.getHallId());
        LOGGER.debug("Performance details: startTime={}, endTime={}, price={}",
            dto.getStartTime(), dto.getEndTime(), dto.getBasePriceCents());
        return performanceService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing performance",
        description = "Updates performance details including start/end time, price, event, and hall. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Performance updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Performance, Event, or Hall not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public PerformanceDto update(@PathVariable Long id, @RequestBody PerformanceDto dto) {
        LOGGER.info("Updating performance with id={} for event={}", id, dto.getEventId());
        LOGGER.debug("Updated performance details: startTime={}, endTime={}, price={}",
            dto.getStartTime(), dto.getEndTime(), dto.getBasePriceCents());
        return performanceService.update(id, dto);
    }
}
