package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.Roles;

public class UserUpdateDto {
    Long userId;
    String email;
    String password;
    String firstName;
    String lastName;
    String country;
    String zipCode;
    String city;
    String address;
    Roles role;

    public UserUpdateDto() {
    }


    public UserUpdateDto(Long userId, String email, String password, String firstName, String lastName, String country, String zipCode, String city, String address, Roles role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.city = city;
        this.address = address;
        this.country = country;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCountry() {
        return "";
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

    public String getAddress() {
        return address;
    }

    public Roles getRole() {
        return role;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(Roles role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return "UserCreateDto{"
            + "userId=" + userId + '\''
            + "email='" + email + '\''
            + ", password='" + password + '\''
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", zipCode='" + zipCode + '\''
            + ", city='" + city + '\''
            + ", address='" + address + '\''
            + '}';
    }
}
