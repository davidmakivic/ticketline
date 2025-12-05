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
    void testGetAllOrders_emptyList() {
        List<OrderDto> orders = orderService.getAllOrders();
        assertThat(orders).isEmpty();
    }

    @Test
    void testGetOrderById_success() {
        Order order = new Order(user, 1500);
        orderRepository.save(order);

        OrderDto dto = orderService.getOrder(order.getId());

        assertThat(dto.id()).isEqualTo(order.getId());
        assertThat(dto.totalPriceCents()).isEqualTo(1500);
    }

    @Test
    void testGetOrder_notFound() {
        assertThatThrownBy(() -> orderService.getOrder(999))
            .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testGetOrdersByUser_success() {
        Order o1 = new Order(user, 1000);
        Order o2 = new Order(user, 2000);
        orderRepository.save(o1);
        orderRepository.save(o2);

        List<OrderDto> list = orderService.getOrdersByUser(user.getUserId());

        assertThat(list).hasSize(2);
    }
}
