package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
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
@RequestMapping("/api/artists")
public class ArtistEndpoint {
    private final ArtistService artistService;

    public ArtistEndpoint(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ArtistDto create(@RequestBody ArtistDto dto) {
        return artistService.create(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ArtistDto update(@PathVariable Long id, @RequestBody ArtistDto dto) {
        return artistService.update(id, dto);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ArtistDto getById(@PathVariable Long id) {
        return artistService.findById(id);
    }


    @PermitAll
    @GetMapping
    public List<ArtistDto> getAll() {
        return artistService.findAll()
            .stream()
            .toList();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        artistService.delete(id);
    }

}
