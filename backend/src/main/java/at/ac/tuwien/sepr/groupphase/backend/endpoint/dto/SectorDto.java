package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;

public class SectorDto {

    private Long id;
    private String name;
    private Long hallId;
    private String priceCategory;
    private SectorType type;

    public SectorDto(Long id, String name, SectorType type, String priceCategory, Long hallId) {
        this.id = id;
        this.name = name;
        this.hallId = hallId;
        this.priceCategory = priceCategory;
        this.type = type;
    }

    public SectorDto(String name, SectorType type, String priceCategory, Long hallId) {
        this.name = name;
        this.hallId = hallId;
        this.priceCategory = priceCategory;
        this.type = type;
    }

    public SectorDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public String getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(String priceCategory) {
        this.priceCategory = priceCategory;
    }

    public SectorType getType() {
        return type;
    }

    public void setType(SectorType type) {
        this.type = type;
    }
}
