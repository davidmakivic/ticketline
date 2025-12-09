package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersEndpoint {

    private final OrderService orderService;

    public OrdersEndpoint(OrderService orderService) {
        this.orderService = orderService;
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable long id) {
        return orderService.getOrder(id);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public List<OrderDto> getByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public OrderDto createOrder(@RequestBody OrderCreateDto createDto) {
        return orderService.createOrder(createDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public OrderDto updateOrder(@PathVariable long id, @RequestBody OrderUpdateDto updateDto) {
        return orderService.updateOrder(id, updateDto);
    }
}
