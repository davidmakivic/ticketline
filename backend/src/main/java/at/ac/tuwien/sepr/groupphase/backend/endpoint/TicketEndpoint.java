package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketStatusUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/tickets")
@Tag(name = "Tickets")
public class TicketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PermitAll
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by id", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching ticket with id={}", id);
        return ticketService.findById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create ticket", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto create(@RequestBody TicketDto dto) {
        LOGGER.info("Creating ticket");
        LOGGER.debug("Request payload: {}", dto);
        return ticketService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Update ticket", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto update(@PathVariable Long id, @RequestBody TicketDto dto) {
        LOGGER.info("Updating ticket with id={}", id);
        LOGGER.debug("Request payload: {}", dto);
        return ticketService.update(id, dto);
    }

    @Secured("ROLE_USER")
    @PutMapping("/{id}/status")
    @Operation(summary = "Update ticket status", security = @SecurityRequirement(name = "apiKey"))
    public TicketDto updateStatus(@PathVariable Long id, @RequestBody TicketStatusUpdateDto dto) {
        LOGGER.info("Updating ticket status for Ticket with id={}", id);
        LOGGER.debug("Request payload: {}", dto);
        return ticketService.updateStatus(id, dto.getStatus(), dto.getVersion());
    }

    @PermitAll
    @GetMapping("/performance/{performanceId}")
    @Operation(summary = "Get tickets by performance id", security = @SecurityRequirement(name = "apiKey"))
    public List<TicketDto> getByPerformanceId(@PathVariable Long performanceId) {
        LOGGER.info("Fetching tickets for performanceId={}", performanceId);
        return ticketService.findByPerformanceId(performanceId);
    }

}