package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PriceCategoryService;
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
@RequestMapping("/api/v1/price-categories")
public class PriceCategoryEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PriceCategoryService service;

    public PriceCategoryEndpoint(PriceCategoryService service) {
        this.service = service;
    }

    @PermitAll
    @GetMapping
    public List<PriceCategoryDto> getAll() {
        LOGGER.info("Fetching all price categories");
        return service.findAll();
    }

    @PermitAll
    @GetMapping("/{id}")
    public PriceCategoryDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching price category with id={}", id);
        return service.findById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public PriceCategoryDto create(@RequestBody PriceCategoryCreateDto dto) {
        LOGGER.info("Creating price category");
        LOGGER.debug("Request payload: {}", dto);
        return service.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public PriceCategoryDto update(@PathVariable Long id, @RequestBody PriceCategoryUpdateDto dto) {
        LOGGER.info("Updating price category with id={}", id);
        LOGGER.debug("Request payload: {}", dto);
        return service.update(id, dto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        LOGGER.info("Deleting price category with id={}", id);
        service.delete(id);
    }
}
