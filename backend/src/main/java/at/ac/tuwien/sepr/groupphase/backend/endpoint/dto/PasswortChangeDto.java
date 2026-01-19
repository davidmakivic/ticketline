package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Holds the data to change the password of the user.
 */
public class PasswortChangeDto {

    //Token is used if user is not authenticated
    String resetToken;

    //Email is used if user is authenticated
    String authenticatedUserEmail;

    //Used if user is authenticated
    String oldPassword;


    @NotBlank(message = "Password must not be empty")
    @Size(max = 32, message = "Password must be at max of length 32")
    @Size(min = 8, message = "Password must be at least of length 8")
    String newPassword;

    public PasswortChangeDto(String resetToken, String password) {
    }

    public PasswortChangeDto() {
    }


    public String getResetToken() {
        return resetToken;
    }

    public String getAuthenticatedUserEmail() {
        return authenticatedUserEmail;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setAuthenticatedUserEmail(String authenticatedUserEmail) {
        this.authenticatedUserEmail = authenticatedUserEmail;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "PasswortChangeDto{"
            + "resetToken='" + resetToken + '\''
            + ", authenticatedUserEmail='" + authenticatedUserEmail + '\''
            + ", oldPassword='" + oldPassword + '\''
            + ", newPassword='" + newPassword + '\''
            + '}';
    }
}
