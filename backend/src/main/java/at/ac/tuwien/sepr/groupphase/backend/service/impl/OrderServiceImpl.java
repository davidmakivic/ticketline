package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    /** Holt alle Orders und mapped sie zu DTOs. */
    @Override
    public List<OrderDto> getAllOrders() {
        return orderMapper.orderListToOrderDtoList(
            orderRepository.findAllByOrderByCreatedAtDesc()
        );
    }

    /** Holt eine Order nach ID und mapped sie zu DTO. */
    @Override
    public OrderDto getOrder(long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.orderToOrderDto(order);
    }

    /** Holt alle Orders eines Users. */
    @Override
    public List<OrderDto> getOrdersByUser(Integer userId) {
        return orderMapper.orderListToOrderDtoList(
            orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId)
        );
    }
}
