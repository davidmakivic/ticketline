package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class HallDto {
    private Long id;
    private Long venueId;
    private String name;
    private JsonNode layoutMetadata;
    private List<SectorDto> sectorIndex;

    public HallDto() {}

    public HallDto(Long id, Long venueId, String name, JsonNode layoutMetadata,  List<SectorDto> sectorIndex) {
        this.id = id;
        this.venueId = venueId;
        this.name = name;
        this.layoutMetadata = layoutMetadata;
        this.sectorIndex = sectorIndex;
    }

    public Long getId() {
        return id;
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

    public List<SectorDto> getSectorIndex() {
        return sectorIndex;
    }

    public void setSectorIndex(List<SectorDto> sectorIndex) {
        this.sectorIndex = sectorIndex;
    }
}
