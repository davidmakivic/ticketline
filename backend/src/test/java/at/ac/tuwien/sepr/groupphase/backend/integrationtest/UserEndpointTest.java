package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserEndpointTest {


    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    void givenUserCreateDto_whenCreateUser_thenReturnCreatedUser() throws Exception {
        UserCreateDto dto = new UserCreateDto("testuser@email.com", "password1", "first", "last", "Austria", "1222", "city", "street", 12, Roles.USER);
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.email").value(dto.getEmail()))
            .andExpect(jsonPath("$.firstName").value(dto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(dto.getLastName()))
            .andExpect(jsonPath("$.zipCode").value(dto.getZipCode()))
            .andExpect(jsonPath("$.city").value(dto.getCity()))
            .andExpect(jsonPath("$.street").value(dto.getStreet()))
            .andExpect(jsonPath("$.houseNumber").value(dto.getHouseNumber()))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.rewardPoints").value(0))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.userStatus").value("UNVERIFIED"))
            .andExpect(jsonPath("$.failedLoginAttempts").value(0));
    }

    @Transactional
    @Test
    void givenUserUpdateDto_whenUpdateUser_thenReturnAndStoreUser() throws Exception {
        ApplicationUser user = new ApplicationUser();
        user.setEmail("testuser@email.com");
        user.setPasswordHash(passwordEncoder.encode("password1"));
        user.setFirstName("first");
        user.setLastName("last");
        user.setCountry("Austria");
        user.setZipCode("1222");
        user.setCity("city");
        user.setStreet("street");
        user.setHouseNumber(12);
        user.setRole(Roles.USER);
        user.setRewardPoints(10);
        user.setUserStatus(UserStatus.UNVERIFIED);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);


        UserUpdateDto updateDto = new UserUpdateDto(
            "updated@email.com",
            "updated@email.com",
            "UpdatedFirst",
            "UpdatedLast",
            "Austria",
            "1337",
            "Vienna",
            "New Address",
            99,
            Roles.USER
        );

        String token = jwtTokenizer.getAuthToken(
            user.getEmail(),
            List.of("ROLE_USER")
        );


        mockMvc.perform(
                put("/api/v1/users/" + user.getUserId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), token)
                    .content(toJson(updateDto))
            )
            // --- Assert (Response Body) ---
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(user.getUserId()))
            .andExpect(jsonPath("$.email").value(updateDto.getEmail()))
            .andExpect(jsonPath("$.firstName").value(updateDto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(updateDto.getLastName()))
            .andExpect(jsonPath("$.zipCode").value(updateDto.getZipCode()))
            .andExpect(jsonPath("$.city").value(updateDto.getCity()))
            .andExpect(jsonPath("$.street").value(updateDto.getStreet()))
            .andExpect(jsonPath("$.houseNumber").value(updateDto.getHouseNumber()))
            .andExpect(jsonPath("$.role").value("USER"));

        // --- Assert (Database State) ---
        ApplicationUser updated = userRepository.getReferenceById(user.getUserId());

        assertAll(
            () -> assertEquals(updateDto.getEmail(), updated.getEmail()),
            () -> assertEquals(updateDto.getFirstName(), updated.getFirstName()),
            () -> assertEquals(updateDto.getLastName(), updated.getLastName()),
            () -> assertEquals(updateDto.getZipCode(), updated.getZipCode()),
            () -> assertEquals(updateDto.getCity(), updated.getCity()),
            () -> assertEquals(updateDto.getStreet(), updated.getStreet()),
            () -> assertEquals(updateDto.getHouseNumber(), updated.getHouseNumber()),
            () -> assertEquals(Roles.USER, updated.getRole())
        );

    }


    @Test
    @Transactional
    void givenUserCreateDto_whenCreateUser_thenStoreUser() throws Exception {
        UserCreateDto dto = new UserCreateDto("testuser@email.com", "password1", "first", "last", "Austria", "1222", "city", "street", 12, Roles.USER);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto)))
            .andReturn();

        String json = result.getResponse()
            .getContentAsString();

        UserDetailDto response = objectMapper.readValue(json, UserDetailDto.class);

        Long id = response.getUserId();
        ApplicationUser user = userRepository.getReferenceById(id);
        assertAll(
            () -> assertEquals(dto.getEmail(), user.getEmail()),
            () -> assertEquals(dto.getFirstName(), user.getFirstName()),
            () -> assertEquals(dto.getLastName(), user.getLastName()),
            () -> assertEquals(dto.getZipCode(), user.getZipCode()),
            () -> assertEquals(dto.getCity(), user.getCity()),
            () -> assertEquals(dto.getStreet(), user.getStreet()),
            () -> assertEquals(dto.getHouseNumber(), user.getHouseNumber()),
            () -> assertEquals(Roles.USER, user.getRole()),
            () -> assertEquals(0, user.getRewardPoints()),
            () -> assertEquals(UserStatus.UNVERIFIED, user.getUserStatus()),
            () -> assertEquals(0, user.getFailedLoginAttempts())
        );
    }

}
