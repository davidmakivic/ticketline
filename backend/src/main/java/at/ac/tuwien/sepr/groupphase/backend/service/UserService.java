package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswortChangeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.exception.GoneException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address.
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);


    /**
     * Creates a new application user.
     *
     * @param dto Dto containing the information of the new user
     * @return the new created application user
     */
    UserDetailDto createApplicationUser(UserCreateDto dto) throws ValidationException, ConflictException;

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Sends an email to reset the password to {@code email}.
     * If there exists no user with {@code email}, no email is sent.
     *
     * @param email email of the account to reset the password
     * @throws MessagingException if sending the email fails
     */
    void resetPassword(String email) throws MessagingException;


    /**
     * Changes the password of a user.
     *
     * @param dto holds the data needed to change the password
     * @throws GoneException     if the given token in {@link PasswortChangeDto} is expired
     * @throws NotFoundException if the token does not exist in the persistent data store
     */
    void changePassword(PasswortChangeDto dto) throws GoneException, NotFoundException, ValidationException;


    /**
     * Changes the data of the user with id {@code id}.
     *
     * @param dto dto containing the new data of the user
     * @return the updated user
     * @throws ValidationException if the update data given for the user is in itself incorrect (no name, …)
     * @throws ConflictException   if the update data given for the user is in conflict with the data currently in the system (user does not exist, …)
     */
    UserDetailDto update(UserUpdateDto dto) throws ValidationException, ConflictException;

    /**
     * Deletes user with id {@code id}.
     *
     * @param id of the user to delete
     */
    void delete(Long id) throws ForbiddenException;

    /**
     * Returns all user depending on the searched data.
     * if status = null, all user are returned
     *
     * @param dto dto containing the searched for parameters
     * @return a list of users with parameters defined in {@code dto}
     */
    List<UserDetailDto> searchUser(UserSearchDto dto) throws ValidationException;

    /**
     * Blocks the user with id {@code id}.
     *
     * @param id id of the user to block
     */
    void blockUser(Long id) throws ForbiddenException;


    /**
     * Unblocks the user with id {@code id}.
     *
     * @param id id of the user to unblock
     */
    void unblockUser(Long id);
}
