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
    @Operation(summary = "Get all performances with pagination", security = @SecurityRequirement(name = "apiKey"))
    public Page<PerformanceDto> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Fetching all performances with page={}, size={}", page, size);
        return performanceService.findAll(page, size);
    }

    @PermitAll
    @GetMapping("/query")
    @Operation(summary = "Search performances with filters", security = @SecurityRequirement(name = "apiKey"))
    public Page<PerformanceDto> searchAdvanced(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String artist,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) EventType eventType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
        @RequestParam(required = false) Integer durationMinutes,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Searching performances: title={}, artist={}, location={}, eventType={}, startDate={}, duration={}",
            title, artist, location, eventType, startDate, durationMinutes);
        return performanceService.findByAdvancedFilters(title, artist, location, eventType, startDate, durationMinutes, page, size);
    }


    @PermitAll
    @GetMapping("/{id}")
    @Operation(summary = "Get performance by id", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching performance with id={}", id);
        return performanceService.findById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto create(@RequestBody PerformanceDto dto) {
        LOGGER.info("Creating performance");
        LOGGER.debug("Request payload: {}", dto);
        return performanceService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Update performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto update(@PathVariable Long id, @RequestBody PerformanceDto dto) {
        LOGGER.info("Updating performance with id={}", id);
        LOGGER.debug("Request payload: {}", dto);
        return performanceService.update(id, dto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete performance", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.info("Deleting performance with id={}", id);
        performanceService.delete(id);
    }
}
