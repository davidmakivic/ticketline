package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswortChangeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.exception.GoneException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;

    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateUser(Principal principal, @RequestBody UserUpdateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Updating user with email={}", principal.getName());
        LOGGER.debug("Request payload: {}", dto);
        dto.setAuthenticatedUserEmail(principal.getName());
        userService.update(dto);
        return ResponseEntity.noContent().build();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws ForbiddenException {
        LOGGER.info("Deleting user with id={}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PermitAll
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailDto createUser(@RequestBody UserCreateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Creating user");
        LOGGER.debug("Request payload: {}", dto.getEmail());
        return userService.createApplicationUser(dto);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailDto createUserasAdmin(@RequestBody UserCreateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Creating user as admin");
        LOGGER.debug("Request payload: {}", dto);
        return userService.createUserAsAdmin(dto);
    }

    @PermitAll
    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam("email") String email) {
        LOGGER.info("Requesting password reset for user with email={}", email);
        try {
            userService.resetPassword(email);
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(Principal principal, @RequestBody PasswortChangeDto dto) throws ValidationException {
        LOGGER.info("Changing password");
        LOGGER.debug("Request payload: {}", dto);

        if (principal != null && principal.getName() != null) {
            LOGGER.info("Changing password for user:{}", principal.getName());
            dto.setAuthenticatedUserEmail(principal.getName());
        }

        try {
            userService.changePassword(dto);
        } catch (GoneException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.GONE);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@Secured("ROLE_ADMIN")
    @PermitAll
    @GetMapping
    public Page<UserDetailDto> searchUsers(
        @RequestParam(required = false) String email,
        @PageableDefault(size = 25, sort = "email") Pageable pageable
    ) throws ValidationException {
        LOGGER.info("Searching users");
        LOGGER.debug("email={}, pageable={}", email, pageable);
        return userService.searchUsers(email, pageable);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/me")
    public UserDetailDto getMe(Principal principal) throws NotFoundException {
        LOGGER.info("Getting user");
        LOGGER.debug("Request payload: {}", principal.getName());

        return userService.getMe(principal.getName());
    }


    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) throws NotFoundException, ForbiddenException {
        LOGGER.info("Blocking user with id={}", id);
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) throws NotFoundException {
        LOGGER.info("Unblocking user with id={}", id);
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }


}
