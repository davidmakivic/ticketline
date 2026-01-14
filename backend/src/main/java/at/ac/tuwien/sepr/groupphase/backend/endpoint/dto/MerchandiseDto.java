package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.ArrayList;
import java.util.List;

public class MerchandiseDto {

    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer quantity;
    private String imageContentType;
    private List<MerchandiseVariantDto> variants = new ArrayList<>(); // Varianten nach Größe



    public MerchandiseDto() {}

    public MerchandiseDto(Long id, String name, String description, Integer price, String imageContentType, List<MerchandiseVariantDto> variants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageContentType = imageContentType;
        this.variants = variants != null ? variants : new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public List<MerchandiseVariantDto> getVariants() {
        return variants;
    }

    public void setVariants(List<MerchandiseVariantDto> variants) {
        this.variants = variants;
    }

}
