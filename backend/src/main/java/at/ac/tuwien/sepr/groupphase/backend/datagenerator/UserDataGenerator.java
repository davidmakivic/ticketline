package at.ac.tuwien.sepr.groupphase.backend.datagenerator;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

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
                .withCountry("Austria")
                .withUserStatus(UserStatus.UNLOCKED)
                .withFailedLoginAttempts(0)
                .build();
            userRepository.save(admin);

            for (int i = 1; i <= NUMBER_OF_USERS_TO_GENERATE; i++) {
                ApplicationUser user = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
                    .withEmail("user" + i + "@email.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("User" + i)
                    .withLastName("UserLastname" + i)
                    .withCountry("Austriaaaaaaaaaaa")
                    .withZipCode("10" + i + "0")
                    .withCity("City" + i)
                    .withAddress("Street " + i)
                    .withRole(Roles.USER)
                    .withRewardPoints(i)
                    .build();

                if (i == 1) {
                    user.setEmail("user@email.com");
                }

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

