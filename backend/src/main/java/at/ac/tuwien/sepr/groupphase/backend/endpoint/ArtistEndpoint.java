package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistEndpoint {
    private final ArtistService artistService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public ArtistEndpoint(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new artist", security = @SecurityRequirement(name = "apiKey"))
    public ArtistDto create(
        @RequestParam("firstName") String firstName,
        @RequestParam("lastName") String lastName,
        @RequestParam("stageName") String stageName,
        @RequestParam("artistType") ArtistType artistType,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        LOGGER.info("Request to create new artist: {} {}", firstName, lastName);
        LOGGER.debug("Create artist payload: firstName={}, lastName={}, stageName={}, type={}, imagePresent={}",
            firstName, lastName, stageName, artistType, image != null);
        return artistService.create(firstName, lastName, stageName, artistType, image);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing artist", security = @SecurityRequirement(name = "apiKey"))
    public ArtistDto update(@PathVariable Long id,
                            @RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam("stageName") String stageName,
                            @RequestParam("artistType") ArtistType artistType,
                            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        LOGGER.info("Request to update artist with id={}", id);
        LOGGER.debug("Update artist payload: firstName={}, lastName={}, stageName={}, type={}, imagePresent={}",
            firstName, lastName, stageName, artistType, image != null);
        return artistService.update(id, firstName, lastName, stageName, artistType, image);
    }

    @PermitAll
    @GetMapping("/{id}")
    @Operation(summary = "Get detailed information about a specific artist", security = @SecurityRequirement(name = "apiKey"))
    public ArtistDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching artist with id={}", id);
        return artistService.findById(id);
    }

    @PermitAll
    @GetMapping("/{id}/image")
    @Operation(summary = "Get image for a specific artist", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<StreamingResponseBody> getArtistImage(@PathVariable Long id) {
        LOGGER.info("Fetching image for artist id={}", id);
        return artistService.streamArtistImage(id);
    }

    @PermitAll
    @GetMapping("/{id}/events")
    @Operation(summary = "Get all events for a specific artist", security = @SecurityRequirement(name = "apiKey"))
    public List<EventDto> getEventsByArtistId(@PathVariable Long id) {
        LOGGER.info("Fetching events for artist id={}", id);
        return artistService.findEventsByArtistId(id);
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get list of all artists", security = @SecurityRequirement(name = "apiKey"))
    public List<ArtistDto> getAll() {
        LOGGER.info("Fetching all artists");
        return artistService.findAll()
            .stream()
            .toList();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an artist", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.info("Deleting artist with id={}", id);
        artistService.delete(id);
    }


    @PermitAll
    @GetMapping("/autocomplete")
    @Operation(summary = "Get artists by name for autocomplete", security = @SecurityRequirement(name = "apiKey"))
    public List<ArtistAutocompleteDto> getByName(
        @RequestParam("name") String name,
        @RequestParam("limit") int limit) {
        LOGGER.info("Fetching artists by name={}", name);
        return artistService.findArtistAutocomplete(name, limit);
    }

}
