package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.VenueService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/venues")
public class VenueEndpoint {

    private final VenueService venueService;

    public VenueEndpoint(VenueService venueService) {
        this.venueService = venueService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public VenueDto createVenue(@RequestBody VenueCreateDto venueDto) {
        return venueService.create(venueDto);
    }

    @PermitAll
    @GetMapping("/{id}")
    public VenueDto getById(@PathVariable("id") Long id) {
        return venueService.findById(id);
    }

    @PermitAll
    @GetMapping
    public List<VenueDto> getAll() {
        return venueService.findAll();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        venueService.delete(id);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public VenueDto updateVenue(@PathVariable("id") Long id, @RequestBody VenueUpdateDto venueDto) {
        return venueService.update(id, venueDto);
    }
}
