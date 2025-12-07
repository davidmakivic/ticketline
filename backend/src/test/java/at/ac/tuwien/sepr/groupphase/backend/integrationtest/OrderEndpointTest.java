package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class OrderEndpointTest {

    private static final String BASE_PATH = "/api/orders";
    private static final String USER_PATH = "/api/orders/user/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        ApplicationUser u = ApplicationUser.ApplicationUserBuilder
            .aApplicationUser()
            .withEmail("test@example.com")
            .withPassword("hashed")
            .withFirstName("Test")
            .withLastName("User")
            .withCountry("Austria")
            .withZipCode("1234")
            .withCity("Vienna")
            .withAddress("Street 1")
            .withRole(Roles.USER)
            .withRewardPoints(0)
            .withUserStatus(UserStatus.UNLOCKED)
            .withFailedLoginAttempts(0)
            .build();

        userRepository.save(u);
    }

    @Test
    void getAllOrders_whenNoOrdersExist_returnsEmptyList() throws Exception {
        mockMvc.perform(get(BASE_PATH)
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getOrder_whenOrderDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/999")
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersByUser_whenUserHasOrders_returnsOrderList() throws Exception {
        ApplicationUser u = userRepository.findAll().get(0);

        Order o = new Order(u, 1500);
        orderRepository.save(o);

        mockMvc.perform(get(USER_PATH + u.getUserId())
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].totalPriceCents").value(1500));
    }
}
