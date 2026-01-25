package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new artist",
        description = "Creates a new artist with first name, last name, stage name, type and optional image. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Artist created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
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
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update an existing artist",
        description = "Updates artist details including first name, last name, stage name, type and optional image. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Artist updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
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
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get detailed information about a specific artist",
        description = "Returns comprehensive details about an artist including all associated events. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Artist found"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ArtistDto getById(@PathVariable Long id) {
        LOGGER.info("Fetching artist with id={}", id);
        return artistService.findById(id);
    }

    @PermitAll
    @GetMapping("/{id}/image")
    @Operation(
        summary = "Get image for a specific artist",
        description = "Returns the image associated with an artist in its original format. "
            + "Returns 204 No Content if no image is available. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No image available for this artist"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<byte[]> getArtistImage(@PathVariable Long id) {
        LOGGER.info("Fetching image for artist id={}", id);
        return artistService.getArtistImage(id);
    }

    @PermitAll
    @GetMapping("/{id}/events")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all events for a specific artist",
        description = "Returns a list of all events associated with an artist. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public List<EventDto> getEventsByArtistId(@PathVariable Long id) {
        LOGGER.info("Fetching events for artist id={}", id);
        return artistService.findEventsByArtistId(id);
    }

    @PermitAll
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get list of all artists",
        description = "Returns a list of all artists in the system. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved artists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public List<ArtistDto> getAll() {
        LOGGER.info("Fetching all artists");
        return artistService.findAll()
            .stream()
            .toList();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete an artist",
        description = "Permanently deletes an artist and removes associations with events. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Artist deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Artist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public void delete(@PathVariable Long id) {
        LOGGER.info("Deleting artist with id={}", id);
        artistService.delete(id);
    }


    @PermitAll
    @GetMapping("/autocomplete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get artists by name for autocomplete",
        description = "Returns a limited list of artists matching the provided name for autocomplete functionality. "
            + "Publicly accessible.",
        parameters = {
            @Parameter(name = "name", description = "Artist name to search for (firstName, lastName, or stageName)", example = "John"),
            @Parameter(name = "limit", description = "Maximum number of results to return", example = "10")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved autocomplete suggestions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public List<ArtistAutocompleteDto> getByName(
        @RequestParam("name") String name,
        @RequestParam("limit") int limit) {
        LOGGER.info("Fetching artists by name={}", name);
        return artistService.findArtistAutocomplete(name, limit);
    }

}
