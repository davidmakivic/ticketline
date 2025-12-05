package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class VenueDto {
    private Long id;
    private String name;
    private String street;
    private String city;
    private String country;
    private String postalCode;

    public VenueDto() {}

    public VenueDto(Long id, String name, String street, String city, String country, String postalCode) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
