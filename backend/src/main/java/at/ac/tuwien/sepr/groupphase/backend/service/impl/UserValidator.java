package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void validateUserForCreate(UserCreateDto dto) throws ValidationException, ConflictException {

        List<String> errors = new ArrayList<>();

        if (dto.getEmail() == null || dto.getEmail()
            .isBlank()) {
            errors.add("Email must not be empty");
        } else {
            if (dto.getEmail()
                .length() > 255) {
                errors.add("Email must not exceed 255 characters");
            }
            // Validation following RFC 5322
            // source: https://www.baeldung.com/java-email-validation-regex#bd-regular-expression-by-rfc-5322-for-email-validation
            if (!dto.getEmail()
                .matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                errors.add("Email must be a valid email address");
            }
        }

        if (dto.getPassword() == null || dto.getPassword()
            .isBlank()) {
            errors.add("Password must not be empty");
        } else {
            if (dto.getPassword()
                .length() < 8) {
                errors.add("Password must be at least 8 characters long");
            }
        }

        if (dto.getFirstName() == null || dto.getFirstName()
            .isBlank()) {
            errors.add("First name must not be empty");
        } else if (dto.getFirstName()
            .length() > 255) {
            errors.add("First name must not exceed 255 characters");
        }

        if (dto.getLastName() == null || dto.getLastName()
            .isBlank()) {
            errors.add("Last name must not be empty");
        } else if (dto.getLastName()
            .length() > 255) {
            errors.add("Last name must not exceed 255 characters");
        }
        if (dto.getCountry() == null || dto.getCountry().isBlank()) {
            if (dto.getZipCode() == null || dto.getZipCode()
                .isBlank()) {
                errors.add("ZIP code must not be empty");
            }
        }

        if (dto.getCity() == null || dto.getCity()
            .isBlank()) {
            errors.add("City must not be empty");
        } else if (dto.getCity()
            .length() > 255) {
            errors.add("City must not exceed 255 characters");
        }

        if (dto.getAddress() == null || dto.getAddress()
            .isBlank()) {
            errors.add("Address must not be empty");
        } else if (dto.getAddress()
            .length() > 255) {
            errors.add("Address must not exceed 255 characters");
        }

        if (dto.getRole() == null) {
            errors.add("Role must not be null");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation of user data to create failed", errors);
        }
        boolean isAdmin = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority()
                .equals("ROLE_ADMIN"));

        if (!isAdmin && dto.getRole() == Roles.ADMIN) {
            errors.add("Only admins can create admin accounts");
        }

        if (userRepository.findUserByEmail(dto.getEmail()) != null) {
            errors.add("User with this email already exists");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("User creation failed due to conflicts", errors);
        }
    }

}
