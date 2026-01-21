package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservations")
public class ReservationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ReservationService reservationService;

    public ReservationEndpoint(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Create reservation", security = @SecurityRequirement(name = "apiKey"))
    public ReservationDto create(@RequestBody ReservationCreateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("POST /api/v1/reservations");
        return reservationService.create(dto);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get my reservations", security = @SecurityRequirement(name = "apiKey"))
    public List<ReservationDto> getMyReservations(Principal principal) {
        LOGGER.info("GET /api/v1/reservations");
        return reservationService.getAllForUser(principal.getName());
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete my reservation", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable long id, Principal principal) {
        LOGGER.info("DELETE /api/v1/reservations/{}", id);
        reservationService.deleteForUser(id, principal.getName());
    }
}
