package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UserValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void validateUserForCreate(UserCreateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Validating user for creation: {}", dto.getEmail());
        LOGGER.debug("Payload: {}", dto);

        List<String> errors = new ArrayList<>();

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.add("Email darf nicht leer sein");
        } else {
            if (dto.getEmail().length() > 255) {
                errors.add("Email darf nicht länger als 255 Zeichen sein");
            }
            // Validation following RFC 5322
            // source: https://www.baeldung.com/java-email-validation-regex#bd-regular-expression-by-rfc-5322-for-email-validation
            if (!dto.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                errors.add("Email muss valide sein");
            }
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("Passwort darf nicht leer sein");
        } else {
            if (dto.getPassword().length() < 8) {
                errors.add("Passwort muss mindestens 8 Zeichen enthalten");
            }
        }

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            errors.add("Vorname darf nicht leer sein");
        } else if (dto.getFirstName().length() > 255) {
            errors.add("Vorname darf nicht länger als 255 Zeichen sein");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            errors.add("Nachname darf nicht leer sein");
        } else if (dto.getLastName().length() > 255) {
            errors.add("Nachname darf nicht länger als 255 Zeichen sein");
        }
        if (dto.getCountry() == null || dto.getCountry().isBlank()) {
            errors.add("Land darf nicht leer sein");
        } else if (dto.getCountry().length() > 255) {
            errors.add("Land darf nicht leer als 255 Zeichen sein");
        }
        if (dto.getZipCode() == null || dto.getZipCode().isBlank()) {
            errors.add("Postleitzahl darf nicht leer sein");
        }

        if (dto.getCity() == null || dto.getCity().isBlank()) {
            errors.add("Ort darf nicht leer sein");
        } else if (dto.getCity().length() > 255) {
            errors.add("Ort darf nicht länger als 255 Zeichen sein");
        }

        if (dto.getStreet() == null || dto.getStreet().isBlank()) {
            errors.add("Adresse darf nicht leer sein");
        } else if (dto.getStreet().length() > 255) {
            errors.add("Adresse darf nicht länger als 255 Zeichen sein");
        }

        if (dto.getHouseNumber() == null) {
            errors.add("Hausnummer darf nicht leer sein");
        } else if (dto.getHouseNumber() <= 0) {
            errors.add("Hausnummer kann nicht kleiner als 1 sein");
        }

        if (dto.getRole() == null) {
            errors.add("Rolle darf nicht leer sein");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation of user data to create failed", errors);
        }
        boolean isAdmin = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && dto.getRole() == Roles.ADMIN) {
            errors.add("Nur admins dürfen Admin Accounts erstellen");
        }

        if (userRepository.findUserByEmail(dto.getEmail()) != null) {
            errors.add("Diese Email ist bereits registriert");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("User creation failed due to conflicts", errors);
        }
    }

    public void validateUserForUpdate(UserUpdateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Validating user for update: {}", dto.getAuthenticatedUserEmail());
        LOGGER.debug("Payload: {}", dto);

        ApplicationUser userToUpdate = userRepository.findUserByEmail(dto.getAuthenticatedUserEmail());
        if (userToUpdate == null) {
            throw new NotFoundException("User with email " + dto.getAuthenticatedUserEmail() + " does not exist");
        }
        List<String> errors = new ArrayList<>();

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.add("Email must not be empty");
        } else {
            if (dto.getEmail().length() > 255) {
                errors.add("Email must not exceed 255 characters");
            }
            // Validation following RFC 5322
            // source: https://www.baeldung.com/java-email-validation-regex#bd-regular-expression-by-rfc-5322-for-email-validation
            if (!dto.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                errors.add("Email must be a valid email address");
            }
        }

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            errors.add("Vorname darf nicht leer sein");
        } else if (dto.getFirstName().length() > 255) {
            errors.add("Vorname darf nicht länger als 255 Zeichen sein");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            errors.add("Nachname darf nicht leer sein");
        } else if (dto.getLastName().length() > 255) {
            errors.add("Nachname darf nicht länger als 255 Zeichen sein");
        }
        if (dto.getCountry() == null || dto.getCountry().isBlank()) {
            errors.add("Land darf nicht leer sein");
        } else if (dto.getCountry().length() > 255) {
            errors.add("Land darf nicht leer als 255 Zeichen sein");
        }
        if (dto.getZipCode() == null || dto.getZipCode().isBlank()) {
            errors.add("Postleitzahl darf nicht leer sein");
        }

        if (dto.getCity() == null || dto.getCity().isBlank()) {
            errors.add("Ort darf nicht leer sein");
        } else if (dto.getCity().length() > 255) {
            errors.add("Ort darf nicht länger als 255 Zeichen sein");
        }

        if (dto.getStreet() == null || dto.getStreet().isBlank()) {
            errors.add("Adresse darf nicht leer sein");
        } else if (dto.getStreet().length() > 255) {
            errors.add("Adresse darf nicht länger als 255 Zeichen sein");
        }

        if (dto.getHouseNumber() == null) {
            errors.add("Hausnummer darf nicht leer sein");
        } else if (dto.getHouseNumber() <= 0) {
            errors.add("Hausnummer kann nicht kleiner als 1 sein");
        }

        if (dto.getRole() == null) {
            errors.add("Rolle darf nicht leer sein");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation of user data to update failed", errors);
        }
        boolean isAdmin = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));


        if (!isAdmin) {
            if (dto.getRole() == Roles.ADMIN) {
                errors.add("Only admins can update admin accounts");
            }
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            ApplicationUser currentUser = userRepository.findUserByEmail(email);
            if (!userToUpdate.getUserId().equals(currentUser.getUserId())) {
                errors.add("User can only update their own accounts");
            }
        }

        ApplicationUser userWithGivenEmail = userRepository.findUserByEmail(dto.getEmail());
        if (userWithGivenEmail != null && !userWithGivenEmail.getEmail().equals(dto.getAuthenticatedUserEmail())) {
            errors.add("Email already used by another user");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("User update failed due to conflicts", errors);
        }
    }

    public void validateForDelete(String email) throws ForbiddenException {
        LOGGER.info("Validating delete for user id={}", email);

        boolean isAdmin = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            throw new ForbiddenException("Admin Accounts können nicht gelöscht werden");
        }


    }

    public void validatePassword(String password) throws ValidationException {
        LOGGER.info("Validating password");
        LOGGER.debug("PasswordLength={}", password != null ? password.length() : null);
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Validation for password failed", Collections.singletonList("Password must not be empty"));
        }
        if (password.length() < 8) {
            throw new ValidationException("Validation for password failed", Collections.singletonList("Password must be at least 8 characters"));
        }
    }


}
