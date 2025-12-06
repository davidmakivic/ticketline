package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class HallDto {
    private Long id;
    private Long venueId;
    private String name;
    private JsonNode layoutMetadata;

    public HallDto() {}

    public HallDto(Long id, Long venueId, String name, JsonNode layoutMetadata) {
        this.id = id;
        this.venueId = venueId;
        this.name = name;
        this.layoutMetadata = layoutMetadata;
    }

    public Long getId() {
        return id;
    }

    public Long getVenueId() {
        return venueId;
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
