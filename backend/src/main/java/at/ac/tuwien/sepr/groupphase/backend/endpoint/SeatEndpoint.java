package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SeatMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.SeatService;

import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
public class SeatEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SeatService seatService;

    public SeatEndpoint(SeatService seatService) {
        this.seatService = seatService;
    }

    // USER: get seat by id
    @PermitAll
    @GetMapping("/{id}")
    public SeatDto getById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/seats/{}", id);
        return seatService.findById(id);
    }

    // USER: get all seats
    @PermitAll
    @GetMapping
    public List<SeatDto> getAll() {
        LOGGER.info("GET /api/v1/seats");
        return seatService.findAll();
    }

    // USER: get seats of a sector (optional but very useful)
    @PermitAll
    @GetMapping("/sector/{sectorId}")
    public List<SeatDto> getBySector(@PathVariable Long sectorId) {
        return seatService.findBySectorId(sectorId);
    }

    // ADMIN: create seat
    @Secured("ROLE_ADMIN")
    @PostMapping
    public SeatDto create(@RequestBody SeatCreateDto dto) {
        LOGGER.info("POST /api/v1/seats: {}", dto);
        return seatService.create(dto);
    }

    // ADMIN: update seat
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public SeatDto update(@PathVariable Long id, @RequestBody SeatUpdateDto dto) {
        return seatService.update(id, dto);
    }

    // ADMIN: delete seat
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE /seats/{id}", id);
        seatService.delete(id);
    }
}
