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

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("Password must not be empty");
        } else {
            if (dto.getPassword().length() < 8) {
                errors.add("Password must be at least 8 characters long");
            }
        }

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            errors.add("First name must not be empty");
        } else if (dto.getFirstName().length() > 255) {
            errors.add("First name must not exceed 255 characters");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            errors.add("Last name must not be empty");
        } else if (dto.getLastName().length() > 255) {
            errors.add("Last name must not exceed 255 characters");
        }
        if (dto.getCountry() == null || dto.getCountry().isBlank()) {
            errors.add("Country must not be empty");
        }
        if (dto.getZipCode() == null || dto.getZipCode().isBlank()) {
            errors.add("ZIP code must not be empty");
        }

        if (dto.getCity() == null || dto.getCity().isBlank()) {
            errors.add("City must not be empty");
        } else if (dto.getCity().length() > 255) {
            errors.add("City must not exceed 255 characters");
        }

        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            errors.add("Address must not be empty");
        } else if (dto.getAddress().length() > 255) {
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
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

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

    public void validateUserForUpdate(UserUpdateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Validating user for update: {}", dto.getUserId());
        LOGGER.debug("Payload: {}", dto);

        ApplicationUser userToUpdate = userRepository.findUserByUserId(dto.getUserId());
        if (userToUpdate == null) {
            throw new NotFoundException("User with id " + dto.getUserId() + " does not exist");
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
            errors.add("First name must not be empty");
        } else if (dto.getFirstName().length() > 255) {
            errors.add("First name must not exceed 255 characters");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            errors.add("Last name must not be empty");
        } else if (dto.getLastName().length() > 255) {
            errors.add("Last name must not exceed 255 characters");
        }
        if (dto.getCountry() == null || dto.getCountry().isBlank()) {
            if (dto.getZipCode() == null || dto.getZipCode().isBlank()) {
                errors.add("ZIP code must not be empty");
            }
        }

        if (dto.getCity() == null || dto.getCity().isBlank()) {
            errors.add("City must not be empty");
        } else if (dto.getCity().length() > 255) {
            errors.add("City must not exceed 255 characters");
        }

        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            errors.add("Address must not be empty");
        } else if (dto.getAddress().length() > 255) {
            errors.add("Address must not exceed 255 characters");
        }

        if (dto.getRole() == null) {
            errors.add("Role must not be null");
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
        if (userWithGivenEmail != null && !userWithGivenEmail.getUserId().equals(dto.getUserId())) {
            errors.add("Email already used by another user");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("User update failed due to conflicts", errors);
        }
    }

    public void validateForDelete(Long id) throws ForbiddenException {
        LOGGER.info("Validating delete for user id={}", id);

        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser currentUser = userRepository.findUserByEmail(userMail);
        ApplicationUser userToDelete = userRepository.findUserByUserId(id);

        if (userToDelete == null) {
            throw new NotFoundException("User not found");
        }

        boolean isAdmin = currentUser.getRole() == Roles.ADMIN;
        boolean isSelf = currentUser.getUserId().equals(userToDelete.getUserId());

        if (isAdmin && isSelf) {
            throw new ForbiddenException("Admins cannot delete their own account");
        }

        if (!isAdmin && !isSelf) {
            throw new NotFoundException("User not found");
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
