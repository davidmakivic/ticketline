package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;

public class SectorDto {

    private Long id;
    private String name;
    private Long hallId;
    private SectorType type;
    private Long priceCategoryId;
    private String sectorKey;

    public SectorDto(Long id, String name, SectorType type, Long hallId, Long priceCategoryId) {
        this.id = id;
        this.name = name;
        this.hallId = hallId;
        this.type = type;
        this.priceCategoryId = priceCategoryId;
    }

    public SectorDto(String name, SectorType type, Long hallId,  Long priceCategoryId,  String sectorKey) {
        this.name = name;
        this.hallId = hallId;
        this.type = type;
        this.priceCategoryId = priceCategoryId;
        this.sectorKey = sectorKey;
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

    public SectorType getType() {
        return type;
    }

    public void setType(SectorType type) {
        this.type = type;
    }

    public Long getPriceCategoryId() {
        return priceCategoryId;
    }

    public void setPriceCategoryId(Long priceCategoryId) {
        this.priceCategoryId = priceCategoryId;
    }

    public String getSectorKey() {
        return sectorKey;
    }

    public void setSectorKey(String sectorKey) {
        this.sectorKey = sectorKey;
    }
}
