package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class PriceCategoryDto {

    private Long id;
    private double price;
    private String priceCategory;


    public PriceCategoryDto(String priceCategory, double price) {
        this.priceCategory = priceCategory;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(String priceCategory) {
        this.priceCategory = priceCategory;
    }

}
