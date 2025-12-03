package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.SectorService;
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
@RequestMapping("/api/sectors")
public class SectorEndpoint {

    private final SectorService sectorService;
    private final SectorMapper sectorMapper;

    public SectorEndpoint(SectorService sectorService, SectorMapper sectorMapper) {
        this.sectorService = sectorService;
        this.sectorMapper = sectorMapper;
    }

    @PermitAll
    @GetMapping("/{id}")
    public SectorDto getById(@PathVariable Long id) {
        return sectorService.findById(id);
    }

    @PermitAll
    @GetMapping
    public List<SectorDto> getAll() {
        return sectorService.findAll();
    }

    @PermitAll
    @GetMapping("/hall/{hallId}")
    public List<SectorDto> getByHall(@PathVariable Long hallId) {
        return sectorService.findByHallId(hallId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public SectorDto create(@RequestBody SectorDto dto) {
        //var saved = sectorService.create(sectorMapper.sectorDtoToSector(dto));
        return sectorService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public SectorDto update(@PathVariable Long id, @RequestBody SectorDto dto) {
        //var updated = sectorService.update(id, sectorMapper.sectorDtoToSector(dto));
        return sectorService.update(id, sectorMapper.sectorDtoToSector(dto));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sectorService.delete(id);
    }
}
