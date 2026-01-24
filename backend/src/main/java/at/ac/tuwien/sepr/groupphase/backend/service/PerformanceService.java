package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * Service interface for managing performances.
 * Provides operations for creating, updating, retrieving, and deleting performances,
 * as well as querying performances with various filter criteria.
 */
public interface PerformanceService {

    /**
     * Creates a new performance and automatically generates tickets for all seats in the associated hall.
     *
     * @param dto the performance data transfer object containing performance details (required)
     * @return the created {@link PerformanceDto}
     * @throws NotFoundException if the referenced event or hall does not exist
     */
    PerformanceDto create(PerformanceDto dto);

    /**
     * Updates an existing performance with new details.
     * Updates start/end time, price, event, and hall associations.
     *
     * @param id the performance ID (required)
     * @param dto the performance data transfer object with updated details (required)
     * @return the updated {@link PerformanceDto}
     * @throws NotFoundException if performance, event, or hall with given ID does not exist
     */
    PerformanceDto update(Long id, PerformanceDto dto);

    /**
     * Retrieves detailed information about a specific performance.
     *
     * @param id the performance ID (required)
     * @return the {@link PerformanceDto} with event, hall, and venue details
     * @throws NotFoundException if performance with given ID does not exist
     */
    PerformanceDto findById(Long id);

    /**
     * Retrieves a paginated list of all performances.
     *
     * @param page the page number (0-indexed)
     * @param size the number of items per page
     * @return a {@link Page} of {@link PerformanceDto} sorted by start time
     */
    Page<PerformanceDto> findAll(int page, int size);

    /**
     * Searches for performances using advanced filter criteria with pagination.
     * All filter parameters are optional and can be combined.
     *
     * @param title filter by event title (case-insensitive, partial match, optional)
     * @param artist filter by artist name (firstName, lastName, or stageName, optional)
     * @param location filter by venue name, city, or country (optional)
     * @param eventType filter by event category (CONCERT, FESTIVAL, MUSICAL, optional)
     * @param startDate filter by performance start date in ISO format (optional)
     * @param durationMinutes filter by event duration in minutes (optional)
     * @param page the page number (0-indexed)
     * @param size the number of items per page
     * @return a {@link Page} of {@link PerformanceDto} matching the filters
     */
    Page<PerformanceDto> findByAdvancedFilters(
        String title, String artist, String location,
        EventType eventType, Date startDate, Integer durationMinutes,
        int page, int size);
}
