package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/merchandise")
public class MerchandiseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MerchandiseService service;

    public MerchandiseEndpoint(MerchandiseService service) {
        this.service = service;
    }

    @PermitAll
    @GetMapping
    //@Secured({"ROLE_ADMIN", "ROLE_USER"})
    public List<MerchandiseDto> getAll() {
        LOGGER.info("Fetching all merchandise articles");
        return service.findAll();
    }

    @PermitAll
    @GetMapping("/{id}")
    //@Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<MerchandiseDto> getById(@PathVariable Long id) {
        LOGGER.info("Fetching merchandise with id={}", id);
        MerchandiseDto dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    public MerchandiseDto create(@RequestBody MerchandiseDto dto) {
        LOGGER.info("Creating Merchandise article");
        LOGGER.debug("Request payload: {}", dto);
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOGGER.info("Deleting merchandise article with id={}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PermitAll
    @GetMapping("/{id}/image")
    public ResponseEntity<StreamingResponseBody>  getMerchandiseImage(@PathVariable Long id) {
        LOGGER.info("Fetching image for merchandise id={}", id);
        return service.streamMerchandiseImage(id);
    }
}
