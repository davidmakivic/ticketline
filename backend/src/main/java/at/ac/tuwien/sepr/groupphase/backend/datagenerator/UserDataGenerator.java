package at.ac.tuwien.sepr.groupphase.backend.datagenerator;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class UserDataGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_USERS_TO_GENERATE = 10;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void generateUser() {
        if (!userRepository.findAll().isEmpty()) {
            LOG.debug("user already generated");
        } else {
            LOG.debug("generating {} user entries", NUMBER_OF_USERS_TO_GENERATE);
            ApplicationUser admin = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
                .withEmail("admin@email.com")
                .withPassword(passwordEncoder.encode("password"))
                .withFirstName("Admin")
                .withLastName("Admin")
                .withZipCode("1040")
                .withCity("Wien")
                .withAddress("Wiedner Hauptstraße 78")
                .withRole(Roles.ADMIN)
                .withRewardPoints(0)
                .withCreatedAt(LocalDateTime.of(2022, 6, 7, 21, 33))
                .withUserStatus(UserStatus.UNLOCKED)
                .withFailedLoginAttempts(0)
                .build();
            userRepository.save(admin);

            for (int i = 1; i <= NUMBER_OF_USERS_TO_GENERATE; i++) {
                ApplicationUser user = new ApplicationUser();
                user.setEmail("user" + i + "@email.com");
                user.setPasswordHash(passwordEncoder.encode("password"));
                user.setFirstName("User" + i);
                user.setLastName("UserLastname" + i);
                user.setZipCode("10" + i + "0");
                user.setCity("City" + i);
                user.setAddress("Street " + i);
                user.setRole(Roles.USER);
                user.setRewardPoints(i);
                user.setCreatedAt(LocalDateTime.of(2025, 11, 2, 3, 4));
                if (i % 2 == 0) {
                    user.setUserStatus(UserStatus.LOCKED);
                    user.setFailedLoginAttempts(5);
                } else {
                    user.setUserStatus(UserStatus.UNLOCKED);
                    user.setFailedLoginAttempts(i % 5);
                }
                userRepository.save(user);
            }
        }
    }
}

