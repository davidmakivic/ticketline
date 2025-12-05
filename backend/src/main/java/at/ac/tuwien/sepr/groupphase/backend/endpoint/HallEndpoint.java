package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.HallService;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/halls")
public class HallEndpoint {

    private final HallService hallService;

    public HallEndpoint(HallService hallService) {
        this.hallService = hallService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public HallDto createHall(@RequestBody HallCreateDto hall) {
        return hallService.createHall(hall);
    }

    @PermitAll
    @GetMapping("/{id}")
    public HallDto getById(@PathVariable Long id) {
        return hallService.getHallbyId(id);
    }

    @PermitAll
    @GetMapping
    public List<HallDto> getAll() {
        return hallService.findAll();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public HallDto update(@RequestBody HallUpdateDto hall, @PathVariable Long id) {
        return hallService.updateHall(id, hall);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        hallService.deleteHall(id);
    }
}