package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.HallService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("api/v1/halls")
public class HallEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallService hallService;

    public HallEndpoint(HallService hallService) {
        this.hallService = hallService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public HallDto createHall(@RequestBody HallCreateDto hall) {
        LOGGER.info("POST /api/v1/hall: {}", hall);
        return hallService.createHall(hall);
    }

    @PermitAll
    @GetMapping("/{id}")
    public HallDto getById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/halls/{}", id);
        return hallService.getHallbyId(id);
    }

    @PermitAll
    @GetMapping
    public List<HallDto> getAll() {
        LOGGER.info("GET /api/v1/halls");
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
        LOGGER.info("DELETE /halls/{id}", id);
        hallService.deleteHall(id);
    }
}