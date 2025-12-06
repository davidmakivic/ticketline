package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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


    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateUser_shouldReturnCreatedUser() throws Exception {
        UserCreateDto dto = new UserCreateDto("testuser@email.com", "password1", "first", "last", "Austria", "1222", "city", "street 12", Roles.USER);
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.email").value(dto.getEmail()))
            .andExpect(jsonPath("$.firstName").value(dto.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(dto.getLastName()))
            .andExpect(jsonPath("$.zipCode").value(dto.getZipCode()))
            .andExpect(jsonPath("$.city").value(dto.getCity()))
            .andExpect(jsonPath("$.address").value(dto.getAddress()))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.rewardPoints").value(0))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.userStatus").value("UNVERIFIED"))
            .andExpect(jsonPath("$.failedLoginAttempts").value(0));
    }

    @Test
    @Transactional
    void testCreateUser_shouldStoreCreatedUser() throws Exception {
        UserCreateDto dto = new UserCreateDto("testuser@email.com", "password1", "first", "last", "Austria", "1222", "city", "street 12", Roles.USER);

        MvcResult result = mockMvc.perform(post("/api/users")
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
            () -> assertEquals(dto.getAddress(), user.getAddress()),
            () -> assertEquals(Roles.USER, user.getRole()),
            () -> assertEquals(0, user.getRewardPoints()),
            () -> assertEquals(UserStatus.UNVERIFIED, user.getUserStatus()),
            () -> assertEquals(0, user.getFailedLoginAttempts())
        );
    }


}
