package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HallCreateDto {

    @NotNull
    private Long venueId;

    @NotBlank
    private String name;

    @NotNull
    private JsonNode layoutMetadata;

    public HallCreateDto() {}

    public HallCreateDto(Long venueId, String name, JsonNode layoutMetadata) {
        this.layoutMetadata = layoutMetadata;
        this.venueId = venueId;
        this.name = name;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getLayoutMetadata() {
        return layoutMetadata;
    }

    public void setLayoutMetadata(JsonNode layoutMetadata) {
        this.layoutMetadata = layoutMetadata;
    }
}
