package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswortChangeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
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
import java.util.List;

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
    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailDto updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("PUT /users/{id}", id);
        dto.setUserId(id);
        return userService.update(dto);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws ForbiddenException {
        LOGGER.info("DELETE /users/{id}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PermitAll
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailDto createUser(@RequestBody UserCreateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("POST /users/create {}", dto.getEmail());

        return userService.createApplicationUser(dto);
    }

    @PermitAll
    @PostMapping("/resetPassword")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam("email") String email) {
        LOGGER.info("POST /users/resetPassword {}", email);
        try {
            userService.resetPassword(email);
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PermitAll
    @PostMapping("/changePassword")
    public ResponseEntity<String> showChangePasswordPage(@RequestBody PasswortChangeDto dto) throws ValidationException {
        LOGGER.info("POST /users/changePassword");
        try {
            userService.changePassword(dto);
        } catch (GoneException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.GONE);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<UserDetailDto> searchUsers(@RequestBody UserSearchDto dto) throws ValidationException {
        return userService.searchUser(dto);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) throws NotFoundException, ForbiddenException {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) throws NotFoundException {
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }


}
