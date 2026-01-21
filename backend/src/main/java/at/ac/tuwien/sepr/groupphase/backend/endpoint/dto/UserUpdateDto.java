package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.Roles;


public class UserUpdateDto {
    String authenticatedUserEmail;

    String email;
    String firstName;
    String lastName;
    String country;
    String zipCode;
    String city;
    String street;
    Integer houseNumber;
    Roles role;

    public UserUpdateDto() {
    }


    public UserUpdateDto(String authenticatedUserEmail, String email, String firstName, String lastName, String country, String zipCode, String city, String street, Integer houseNumber, Roles role) {
        this.authenticatedUserEmail = authenticatedUserEmail;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.country = country;
        this.role = role;
    }

    public String getAuthenticatedUserEmail() {
        return authenticatedUserEmail;
    }


    public String getEmail() {
        return email;
    }


    public String getCountry() {
        return this.country;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public Roles getRole() {
        return role;
    }


    public void setAuthenticatedUserEmail(String authenticatedUserEmail) {
        this.authenticatedUserEmail = authenticatedUserEmail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "UserCreateDto{"
            + "email='" + email + '\''
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", zipCode='" + zipCode + '\''
            + ", city='" + city + '\''
            + ", street='" + street + '\''
            + ", houseNumber=" + houseNumber
            + '}';
    }
}
