package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class MerchandiseVariantDto {

    private String size;       // z.B. "S", "M", "L", "XL" oder null für keine Größe
    private Integer quantity;  // Stückzahl für diese Größe

    public MerchandiseVariantDto(String size, Integer quantity) {
        this.size = size;
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
