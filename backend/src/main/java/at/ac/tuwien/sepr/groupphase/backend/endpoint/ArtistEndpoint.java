package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistEndpoint {
    private final ArtistService artistService;

    public ArtistEndpoint(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArtistDto create(
        @RequestParam("firstName") String firstName,
        @RequestParam("lastName") String lastName,
        @RequestParam("stageName") String stageName,
        @RequestParam("artistType") ArtistType artistType,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return artistService.create(firstName, lastName, stageName, artistType, image);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArtistDto update(@PathVariable Long id,
                            @RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam("stageName") String stageName,
                            @RequestParam("artistType") ArtistType artistType,
                            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return artistService.update(id, firstName, lastName, stageName, artistType, image);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ArtistDto getById(@PathVariable Long id) {
        return artistService.findById(id);
    }

    @PermitAll
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getArtistImage(@PathVariable Long id) {
        return artistService.getArtistImage(id);
    }

    @PermitAll
    @GetMapping("/{id}/events")
    public List<EventDto> getEventsByArtistId(@PathVariable Long id) {
        return artistService.findEventsByArtistId(id);
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
