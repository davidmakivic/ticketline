package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class PriceCategoryDto {

    private Long id;
    private String name;
    private double price;

    public PriceCategoryDto(Long id, String name, double price) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
