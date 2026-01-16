package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class OrderMerchItemCreateDto {
    private Long variantId;
    private Integer quantity;

    public OrderMerchItemCreateDto() {
    }

    public OrderMerchItemCreateDto(Long variantId, Integer quantity) {
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
