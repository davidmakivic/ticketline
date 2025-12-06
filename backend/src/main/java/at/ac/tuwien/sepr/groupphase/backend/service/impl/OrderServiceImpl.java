package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderMapper.orderListToOrderDtoList(
            orderRepository.findAllByOrderByCreatedAtDesc()
        );
    }

    @Override
    public OrderDto getOrder(long id) {
        Order order = orderRepository.findByIdWithTickets(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return orderMapper.orderToOrderDto(order);
        }

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = order.getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        return orderMapper.orderToOrderDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByUser(Long userId) {

        List<Order> orders = orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return orderMapper.orderListToOrderDtoList(orders);
        }

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = !orders.isEmpty() && orders.get(0).getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        return orderMapper.orderListToOrderDtoList(orders);
    }
}