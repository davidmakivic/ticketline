package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class MerchandiseVariantDto {

    private Long id;
    private String size;
    private Integer quantity;

    public MerchandiseVariantDto() {
    }

    public MerchandiseVariantDto(Long id, String size, Integer quantity) {
        this.id = id;
        this.size = size;
        this.quantity = quantity;
    }

    public MerchandiseVariantDto(String size, Integer quantity) {
        this.id = null;
        this.size = size;
        this.quantity = quantity;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
