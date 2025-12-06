package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PriceCategoryService;
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
@RequestMapping("/api/price-categories")
public class PriceCategoryEndpoint {

    private final PriceCategoryService service;

    public PriceCategoryEndpoint(PriceCategoryService service) {
        this.service = service;
    }

    @PermitAll
    @GetMapping
    public List<PriceCategoryDto> getAll() {
        return service.findAll();
    }

    @PermitAll
    @GetMapping("/{id}")
    public PriceCategoryDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public PriceCategoryDto create(@RequestBody PriceCategoryDto dto) {
        return service.save(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public PriceCategoryDto update(@PathVariable Long id, @RequestBody PriceCategoryDto dto) {
        dto.setId(id);
        return service.save(dto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
