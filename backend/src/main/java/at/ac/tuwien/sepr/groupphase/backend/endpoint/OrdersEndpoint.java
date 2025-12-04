package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersEndpoint {

    private final OrderService orderService;

    public OrdersEndpoint(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Gibt alle Orders zurück. */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    /** Gibt eine bestimmte Order zurück. */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable long id) {
        return orderService.getOrder(id);
    }

    /** Gibt alle Orders eines bestimmten Users. */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public List<OrderDto> getByUser(@PathVariable Integer userId) {
        return orderService.getOrdersByUser(userId);
    }
}
