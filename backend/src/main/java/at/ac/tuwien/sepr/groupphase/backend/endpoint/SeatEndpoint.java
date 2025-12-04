package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SeatMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.SeatService;

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
@RequestMapping("/api/seats")
public class SeatEndpoint {
    private final SeatService seatService;
    private final SeatMapper seatMapper;

    public SeatEndpoint(SeatService seatService, SeatMapper seatMapper) {
        this.seatService = seatService;
        this.seatMapper = seatMapper;
    }

    // USER: get seat by id
    @PermitAll
    @GetMapping("/{id}")
    public SeatDto getById(@PathVariable Long id) {
        return seatService.findById(id);
    }

    // USER: get all seats
    @PermitAll
    @GetMapping
    public List<SeatDto> getAll() {
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
    public SeatDto create(@RequestBody SeatDto dto) {
        //var saved = seatService.create(seatMapper.seatDtoToSeat(dto));
        return seatService.create(seatMapper.seatDtoToSeat(dto));
    }

    // ADMIN: update seat
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public SeatDto update(@PathVariable Long id, @RequestBody SeatDto dto) {
        //var updated = seatService.update(id, seatMapper.seatDtoToSeat(dto));
        return seatService.update(id, dto);
    }

    // ADMIN: delete seat
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        seatService.delete(id);
    }
}
