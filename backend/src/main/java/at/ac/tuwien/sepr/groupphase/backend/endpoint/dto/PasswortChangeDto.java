package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Holds the data to change the password of the user.
 *
 * @param token    token to identify the user.
 * @param password the new password.
 */
public record PasswortChangeDto(
    @NotBlank(message = "Token must not be empty")
    String token,

    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least of length 8")
    String password
) {
}
