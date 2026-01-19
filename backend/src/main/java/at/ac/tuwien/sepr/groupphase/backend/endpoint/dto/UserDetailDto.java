package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;

import java.time.LocalDateTime;

public class UserDetailDto {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String country;
    private String zipCode;
    private String city;
    private String street;
    private Integer houseNumber;
    private Roles role;
    private Integer rewardPoints;
    private LocalDateTime createdAt;
    private UserStatus userStatus;
    private Integer failedLoginAttempts;

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
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

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public void setCountry(String country) {
        this.country = country;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
}
