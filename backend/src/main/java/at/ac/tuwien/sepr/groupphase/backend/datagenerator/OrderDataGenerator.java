package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Profile("generateData")
@Component
public class OrderDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int ORDERS_PER_USER = 3;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderDataGenerator(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void generateOrders() {
        if (!orderRepository.findAll().isEmpty()) {
            LOG.debug("orders already generated");
            return;
        }

        LOG.debug("generating test orders for all users");

        List<ApplicationUser> users = userRepository.findAll();

        if (users.isEmpty()) {
            LOG.warn("No users found — cannot generate orders.");
            return;
        }

        for (ApplicationUser user : users) {
            LOG.debug("generating {} orders for user {}", ORDERS_PER_USER, user.getEmail());

            for (int i = 1; i <= ORDERS_PER_USER; i++) {
                Order order = new Order(user, 1000 * i);
                orderRepository.save(order);
            }
        }

        LOG.debug("order generation complete");
    }
}
