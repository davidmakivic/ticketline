package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class OrderMerchItemDto {
    private final Long variantId;
    private final Long merchandiseId;
    private final String merchandiseName;
    private final String size;
    private final Integer quantity;
    private final Long unitPriceCents;

    public OrderMerchItemDto(Long variantId,
                             Long merchandiseId,
                             String merchandiseName,
                             String size,
                             Integer quantity,
                             Long unitPriceCents) {
        this.variantId = variantId;
        this.merchandiseId = merchandiseId;
        this.merchandiseName = merchandiseName;
        this.size = size;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
    }

    public Long getVariantId() {
        return variantId;
    }

    public Long getMerchandiseId() {
        return merchandiseId;
    }

    public String getMerchandiseName() {
        return merchandiseName;
    }

    public String getSize() {
        return size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getUnitPriceCents() {
        return unitPriceCents;
    }
}
