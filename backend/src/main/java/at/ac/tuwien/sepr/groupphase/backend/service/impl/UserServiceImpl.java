package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswortChangeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.exception.GoneException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator userValidator;
    private final EmailServiceImpl emailServiceImpl;
    private final UserMapper userMapper;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordTokenRepository passwordTokenRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer, UserValidator userValidator,
                           EmailServiceImpl emailServiceImpl,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.emailServiceImpl = emailServiceImpl;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("Loading user by email {}", email);
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getRole() == Roles.ADMIN) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return User.builder()
                .username(applicationUser.getEmail())
                .password(applicationUser.getPasswordHash())
                .authorities(grantedAuthorities)
                .accountLocked(applicationUser.getUserStatus() == UserStatus.LOCKED)
                .build();
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.info("Fetching application user by email {}", email);
        ApplicationUser applicationUser = userRepository.findUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException("E-Mail-Adresse oder Passwort ist falsch.");
    }

    @Override
    public UserDetailDto createApplicationUser(UserCreateDto dto) throws ValidationException, ConflictException {

        LOGGER.info("Creating new application user: {}", dto.getEmail());
        LOGGER.debug("Payload: {}", dto);
        userValidator.validateUserForCreate(dto);
        ApplicationUser newUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withEmail(dto.getEmail())
            .withPassword(passwordEncoder.encode(dto.getPassword()))
            .withFirstName(dto.getFirstName())
            .withLastName(dto.getLastName())
            .withCountry(dto.getCountry())
            .withZipCode(dto.getZipCode())
            .withCity(dto.getCity())
            .withStreet(dto.getStreet())
            .withHouseNumber(dto.getHouseNumber())
            .withRole(dto.getRole())
            .withRewardPoints(0)
            .withUserStatus(UserStatus.UNVERIFIED)
            .withFailedLoginAttempts(0)
            .build();

        return userMapper.applicationUserToUserDetailDto(userRepository.save(newUser));
    }

    @Override
    public String login(UserLoginDto userLoginDto) throws UnauthorizedException {
        LOGGER.info("Attempting login for {}", userLoginDto.getEmail());
        UserDetails storedUser = loadUserByUsername(userLoginDto.getEmail());
        if (storedUser == null
            || !storedUser.isAccountNonExpired()
            || !storedUser.isCredentialsNonExpired()
        ) {
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        }
        if (!storedUser.isAccountNonLocked()) {
            throw new UnauthorizedException("Dieser Account wurde gesperrt.");
        }
        if (!passwordEncoder.matches(userLoginDto.getPassword(), storedUser.getPassword())) {

            boolean isAdmin = storedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));

            if (!isAdmin) {
                userRepository.incrementFailedLoginAttempts(userLoginDto.getEmail());
            }
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        }
        List<String> roles = storedUser.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        userRepository.setFailedLoginAttemptsToZero(userLoginDto.getEmail());
        return jwtTokenizer.getAuthToken(storedUser.getUsername(), roles);
    }

    @Override
    @Transactional
    public void resetPassword(String email) throws MessagingException {
        LOGGER.info("Requesting password reset for {}", email);

        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user == null) {
            return;
        }

        PasswordResetToken token = passwordTokenRepository.findByUser(user);

        String tokenUuid = UUID.randomUUID().toString();
        if (token == null) {
            token = new PasswordResetToken(tokenUuid, user);
        } else {
            token.setToken(tokenUuid);
            token.setExpiryDate(LocalDateTime.now().plusHours(24));
        }

        passwordTokenRepository.save(token);
        emailServiceImpl.sendPasswordResetEmail(tokenUuid, email);
    }

    @Override
    @Transactional
    public void changePassword(PasswortChangeDto dto) throws ValidationException, GoneException, NotFoundException {
        LOGGER.info("Changing password");
        LOGGER.debug("Payload: tokenPresent={}", dto.getToken() != null);
        //If user is logged in
        String dtoAuthenticatedUserEmail = dto.getAuthenticatedUserEmail();
        userValidator.validatePassword(dto.getNewPassword());
        if (dtoAuthenticatedUserEmail != null) {
            //Logged in user can change their own password
            ApplicationUser user = userRepository.findUserByEmail(dtoAuthenticatedUserEmail);
            if (!user.getPasswordHash().equals(passwordEncoder.encode(dto.getOldPassword()))) {
                throw new ValidationException("Validation for password Change failed", Collections.singletonList("Das eingegebene Passwort ist nicht korrekt"));
            }
            user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            return;
        }
        PasswordResetToken token = passwordTokenRepository.findByToken(dto.getToken());
        if (token == null) {
            LOGGER.error("Password change failed because token was not found");
            throw new NotFoundException("Token not found");
        }
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            LOGGER.error("Password change failed because token expired");
            throw new GoneException("Token is expired");
        }
        ApplicationUser user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        passwordTokenRepository.removeByUser(user);
    }

    @Override
    public UserDetailDto update(UserUpdateDto dto) throws ValidationException, ConflictException {
        LOGGER.info("Updating user with email {}", dto.getAuthenticatedUserEmail());
        LOGGER.debug("Payload: {}", dto);
        userValidator.validateUserForUpdate(dto);
        ApplicationUser applicationUser = userRepository.findUserByEmail(dto.getAuthenticatedUserEmail());
        applicationUser.setFirstName(dto.getFirstName());
        applicationUser.setLastName(dto.getLastName());
        applicationUser.setEmail(dto.getEmail());
        applicationUser.setCountry(dto.getCountry());
        applicationUser.setZipCode(dto.getZipCode());
        applicationUser.setCity(dto.getCity());
        applicationUser.setStreet(dto.getStreet());
        applicationUser.setHouseNumber(dto.getHouseNumber());
        applicationUser.setRole(dto.getRole());

        return userMapper.applicationUserToUserDetailDto(userRepository.save(applicationUser));
    }

    @Override
    public void delete(Long id) throws ForbiddenException {
        LOGGER.info("Deleting user with id {}", id);
        userValidator.validateForDelete(id);
        if (id == null) {
            return;
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDetailDto> searchUser(UserSearchDto dto) throws ValidationException {
        LOGGER.info("Searching users");
        LOGGER.debug("Payload: {}", dto);
        List<ApplicationUser> users;
        if (dto == null) {
            users = userRepository.findAll();
            return userMapper.applicationUserListToUserDetailDtoList(users);
        }

        switch (dto.userStatus()) {

            case LOCKED: {
                users = userRepository.findAllByUserStatus(UserStatus.LOCKED);
                break;
            }

            case UNLOCKED: {
                users = userRepository.findAllByUserStatus(UserStatus.UNLOCKED);
                break;
            }

            case UNVERIFIED: {
                users = userRepository.findAllByUserStatus(UserStatus.UNVERIFIED);
                break;
            }

            default:
                throw new ValidationException("Validation for search failed", Collections.singletonList("Unknonw User Status"));
        }
        return userMapper.applicationUserListToUserDetailDtoList(users);
    }


    @Override
    public Page<UserDetailDto> searchUsers(String email, Pageable pageable) throws ValidationException {

        Page<ApplicationUser> page;

        if (email == null || email.isBlank()) {
            page = userRepository.findAll(pageable);
        } else {
            page = userRepository.findByEmailContainingIgnoreCase(email.trim(), pageable);
        }

        return page.map(userMapper::applicationUserToUserDetailDto);
    }

    @Override
    public void blockUser(Long id) throws ForbiddenException {
        LOGGER.info("Blocking user with id {}", id);

        ApplicationUser user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Roles.ADMIN) {
            throw new ForbiddenException("Can not block an admin");
        }
        user.setUserStatus(UserStatus.LOCKED);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long id) {
        LOGGER.info("Unblocking user with id {}", id);

        ApplicationUser user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setUserStatus(UserStatus.UNLOCKED);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    @Override
    public UserDetailDto getMe(String email) {
        return userMapper.applicationUserToUserDetailDto(userRepository.getApplicationUserByEmail((email)));
    }
}
