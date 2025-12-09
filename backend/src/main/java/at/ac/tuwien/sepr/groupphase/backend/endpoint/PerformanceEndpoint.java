package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/performances")
@Tag(name = "Performances")
public class PerformanceEndpoint {

    private final PerformanceService performanceService;

    public PerformanceEndpoint(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get all performances", security = @SecurityRequirement(name = "apiKey"))
    public List<PerformanceDto> getAll() {
        return performanceService.findAll();
    }

    @PermitAll
    @GetMapping("/{id}")
    @Operation(summary = "Get performance by id", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto getById(@PathVariable Long id) {
        return performanceService.findById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto create(@RequestBody PerformanceDto dto) {
        return performanceService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Update performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto update(@PathVariable Long id, @RequestBody PerformanceDto dto) {
        return performanceService.update(id, dto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete performance", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        performanceService.delete(id);
    }

}
