package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

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
    @PostMapping
    @Operation(summary = "Create performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto create(@RequestBody PerformanceDto dto) {
        return performanceService.create(dto);
    }

}
