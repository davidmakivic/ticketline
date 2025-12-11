package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.SectorService;
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
@RequestMapping("/api/v1/sectors")
public class SectorEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SectorService sectorService;

    public SectorEndpoint(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @PermitAll
    @GetMapping("/{id}")
    public SectorDto getById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/sectors/{}", id);
        return sectorService.findById(id);
    }

    @PermitAll
    @GetMapping
    public List<SectorDto> getAll() {
        LOGGER.info("GET /api/v1/sectors");
        return sectorService.findAll();
    }

    @PermitAll
    @GetMapping("/hall/{hallId}")
    public List<SectorDto> getByHall(@PathVariable Long hallId) {
        return sectorService.findByHallId(hallId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public SectorDto create(@RequestBody SectorCreateDto dto) {
        LOGGER.info("POST /api/v1/sectors: {}", dto);
        return sectorService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public SectorDto update(@PathVariable Long id, @RequestBody SectorUpdateDto dto) {
        return sectorService.update(id, dto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE /sectors/{id}", id);
        sectorService.delete(id);
    }
}
