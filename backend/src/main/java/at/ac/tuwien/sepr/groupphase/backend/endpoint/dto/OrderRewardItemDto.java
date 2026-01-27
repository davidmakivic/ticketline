package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.MerchandiseSize;

public class OrderRewardItemDto {

    private final Long variantId;
    private final Long merchandiseId;
    private final String merchandiseName;
    private final MerchandiseSize size;
    private final Integer quantity;
    private final Long unitPricePoints;

    public OrderRewardItemDto(Long variantId, Long merchandiseId, String merchandiseName, MerchandiseSize size, Integer quantity, Long unitPricePoints) {
        this.variantId = variantId;
        this.merchandiseId = merchandiseId;
        this.merchandiseName = merchandiseName;
        this.size = size;
        this.quantity = quantity;
        this.unitPricePoints = unitPricePoints;
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

    public MerchandiseSize getSize() {
        return size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getUnitPricePoints() {
        return unitPricePoints;
    }


}