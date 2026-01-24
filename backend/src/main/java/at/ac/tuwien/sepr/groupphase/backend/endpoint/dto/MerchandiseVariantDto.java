package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.MerchandiseSize;

public class MerchandiseVariantDto {

    private Long id;
    private MerchandiseSize size;
    private Integer quantity;

    public MerchandiseVariantDto() {
    }

    public MerchandiseVariantDto(Long id, MerchandiseSize size, Integer quantity) {
        this.id = id;
        this.size = size;
        this.quantity = quantity;
    }

    public MerchandiseVariantDto(MerchandiseSize size, Integer quantity) {
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

    public MerchandiseSize getSize() {
        return size;
    }

    public void setSize(MerchandiseSize size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
