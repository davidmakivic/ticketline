package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswortChangeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.GoneException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
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


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordTokenRepository passwordTokenRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer, UserValidator userValidator, EmailServiceImpl emailServiceImpl) {
        this.userRepository = userRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.emailServiceImpl = emailServiceImpl;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getRole() == Roles.ADMIN) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPasswordHash(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public ApplicationUser createApplicationUser(UserCreateDto dto) throws ValidationException, ConflictException {

        userValidator.validateUserForCreate(dto);
        ApplicationUser newUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withEmail(dto.getEmail())
            .withPassword(passwordEncoder.encode(dto.getPassword()))
            .withFirstName(dto.getFirstName())
            .withLastName(dto.getLastName())
            .withCountry(dto.getCountry())
            .withZipCode(dto.getZipCode())
            .withCity(dto.getCity())
            .withAddress(dto.getAddress())
            .withRole(dto.getRole())
            .withRewardPoints(0)
            .withUserStatus(UserStatus.UNVERIFIED)
            .withFailedLoginAttempts(0)
            .build();

        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    @Transactional
    public void resetPassword(String email) throws MessagingException {

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
    public void changePassword(PasswortChangeDto dto) throws GoneException, NotFoundException {
        PasswordResetToken token = passwordTokenRepository.findByToken(dto.token());
        if (token == null) {
            throw new NotFoundException("Token not found");
        }
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new GoneException("Token is expired");
        }
        ApplicationUser user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
        passwordTokenRepository.removeByUser(user);
    }
}
