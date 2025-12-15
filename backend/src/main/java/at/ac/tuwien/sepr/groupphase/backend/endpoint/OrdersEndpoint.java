package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CancelTicketsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CancellationResultDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderService orderService;

    public OrdersEndpoint(OrderService orderService) {
        this.orderService = orderService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<OrderDto> getAllOrders() {
        LOGGER.info("Fetching all orders");
        LOGGER.debug("Requesting all orders");
        return orderService.getAllOrders();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public List<OrderDto> getMyOrders() {
        LOGGER.info("Fetching my orders");
        return orderService.getMyOrders();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable long id) {
        LOGGER.info("Fetching order with id={}", id);
        return orderService.getOrder(id);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public List<OrderDto> getByUser(@PathVariable Long userId) {
        LOGGER.info("Fetching orders for userId={}", userId);
        return orderService.getOrdersByUser(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public OrderDto createOrder(@RequestBody OrderCreateDto createDto) throws ValidationException, ConflictException {
        LOGGER.info("Creating order");
        LOGGER.debug("Request payload: {}", createDto);
        return orderService.createOrder(createDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public OrderDto updateOrder(@PathVariable long id, @RequestBody OrderUpdateDto updateDto) {
        LOGGER.info("Updating order with id={}", id);
        LOGGER.debug("Request payload: {}", updateDto);
        return orderService.updateOrder(id, updateDto);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{id}/cancel")
    public CancellationResultDto cancelTickets(@PathVariable long id, @RequestBody CancelTicketsDto dto) {
        LOGGER.info("Cancelling tickets for order id={}", id);
        LOGGER.debug("Request payload: {}", dto);
        return orderService.cancelTickets(id, dto.getTicketIds());
    }

}
