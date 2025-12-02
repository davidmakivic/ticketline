package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "zip_code", nullable = false)
    private String zipCode;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;
    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    public ApplicationUser() {
    }

    public ApplicationUser(Integer userId, String email, String passwordHash, String firstName, String lastName,
                           String zipCode, String city, String address, Roles role, Integer rewardPoints,
                           LocalDateTime createdAt, UserStatus status, Integer failedLoginAttempts
    ) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.city = city;
        this.address = address;
        this.role = role;
        this.rewardPoints = rewardPoints;
        this.createdAt = createdAt;
        this.userStatus = status;
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
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

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public static final class ApplicationUserBuilder {
        private Integer userId;
        private String email;
        private String passwordHash;
        private String firstName;
        private String lastName;
        private String zipCode;
        private String city;
        private String address;
        private Roles role;
        private Integer rewardPoints;
        private LocalDateTime createdAt;
        private UserStatus userStatus;
        private Integer failedLoginAttempts;

        private ApplicationUserBuilder() {
        }

        public static ApplicationUser.ApplicationUserBuilder aApplicationUser() {
            return new ApplicationUser.ApplicationUserBuilder();
        }

        public ApplicationUser.ApplicationUserBuilder withId(Integer id) {
            this.userId = id;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withPassword(String password) {
            this.passwordHash = password;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withRole(Roles role) {
            this.role = role;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withRewardPoints(Integer rewardPoints) {
            this.rewardPoints = rewardPoints;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withUserStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public ApplicationUser.ApplicationUserBuilder withFailedLoginAttempts(Integer failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
            return this;
        }

        public ApplicationUser build() {
            ApplicationUser ApplicationUser = new ApplicationUser();
            ApplicationUser.setUserId(userId);
            ApplicationUser.setEmail(email);
            ApplicationUser.setPasswordHash(passwordHash);
            ApplicationUser.setFirstName(firstName);
            ApplicationUser.setLastName(lastName);
            ApplicationUser.setZipCode(zipCode);
            ApplicationUser.setCity(city);
            ApplicationUser.setAddress(address);
            ApplicationUser.setRole(role);
            ApplicationUser.setRewardPoints(rewardPoints);
            ApplicationUser.setCreatedAt(createdAt);
            ApplicationUser.setUserStatus(userStatus);
            ApplicationUser.setFailedLoginAttempts(failedLoginAttempts);
            return ApplicationUser;
        }
    }
}
