package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class PriceCategoryCreateDto {

    private String name;
    private double price;

    public PriceCategoryCreateDto(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
