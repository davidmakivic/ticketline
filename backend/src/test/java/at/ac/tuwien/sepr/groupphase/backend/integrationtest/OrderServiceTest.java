package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ApplicationUser user;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        user = new ApplicationUser();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@test.com");
        user.setPasswordHash("pw");
        user.setRole(Roles.USER);
        user.setUserStatus(UserStatus.UNLOCKED);
        user.setAddress("Teststraße 1");
        user.setCity("Wien");
        user.setZipCode("1010");
        user.setCreatedAt(LocalDateTime.of(2025, 12, 11, 11, 11));
        user.setRewardPoints(0);
        user.setFailedLoginAttempts(0);

        user = userRepository.save(user);
    }

    @Test
    void getAllOrders_whenNoOrdersExist_returnsEmptyList() {
        List<OrderDto> orders = orderService.getAllOrders();
        assertThat(orders).isEmpty();
    }

    @Test
    void getOrder_whenOrderExists_returnsOrderDto() {
        Order order = new Order(user, 1500);
        orderRepository.save(order);

        OrderDto dto = orderService.getOrder(order.getId());

        assertThat(dto.getId()).isEqualTo(order.getId());
        assertThat(dto.getTotalPriceCents()).isEqualTo(1500);
    }

    @Test
    void getOrder_whenOrderDoesNotExist_throwsNotFound() {
        assertThatThrownBy(() -> orderService.getOrder(999L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Order not found");
    }

    @Test
    void getOrdersByUser_whenOrdersExist_returnsListOfOrders() {
        orderRepository.save(new Order(user, 1000));
        orderRepository.save(new Order(user, 2000));

        List<OrderDto> list = orderService.getOrdersByUser(user.getUserId().longValue());

        assertThat(list).hasSize(2);
    }
}
