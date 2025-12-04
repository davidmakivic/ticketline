package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class HallCreateDto {

    private Long venueId;
    private String name;
    private JsonNode layoutMetadata;

    public HallCreateDto() {}

    public HallCreateDto(Long venueId, String name,JsonNode layoutMetadata) {
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
