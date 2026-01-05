package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

    public TicketEndpoint(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
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


    @PermitAll
    @GetMapping("/performance/{performanceId}")
    public List<TicketDto> getByPerformanceId(@PathVariable Long performanceId) {
        Long currentUserId = getCurrentUserIdOrNull();
        List<TicketDto> tickets = ticketService.findByPerformanceId(performanceId);
        for (TicketDto t : tickets) {
            markReservedByMe(t, currentUserId);
        }
        return tickets;
    }

    @PostMapping("/{id}/hold")
    @Secured("ROLE_USER")
    public ResponseEntity<TicketDto> hold(@PathVariable Long id) throws ConflictException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findUserByEmail(email);

        TicketDto dto = ticketService.hold(id, user.getUserId());

        dto.setReservedByMe(true);

        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{id}/hold")
    @Secured("ROLE_USER")
    public ResponseEntity<TicketDto> release(@PathVariable Long id) throws ConflictException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findUserByEmail(email);

        TicketDto dto = ticketService.release(id, user.getUserId());

        dto.setReservedByMe(false);

        return ResponseEntity.ok(dto);
    }


    @PermitAll
    @GetMapping
    @Operation(summary = "Get all tickets", security = @SecurityRequirement(name = "apiKey"))
    public List<TicketDto> getAll() {
        LOGGER.info("Fetching all tickets");
        return ticketService.findAll();
    }

    private Long getCurrentUserIdOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        ApplicationUser u = userRepository.findUserByEmail(auth.getName());
        return u != null ? u.getUserId() : null;
    }

    private void markReservedByMe(TicketDto t, Long currentUserId) {
        boolean byMe = currentUserId != null
            && t.getReservedByUserId() != null
            && t.getReservedByUserId().equals(currentUserId);
        t.setReservedByMe(byMe);
    }

}