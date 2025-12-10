package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;


    public OrderServiceImpl(
        OrderRepository orderRepository,
        UserRepository userRepository,
        TicketRepository ticketRepository,
        OrderMapper orderMapper) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
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
            .orElseThrow(() -> new NotFoundException("Order not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return orderMapper.orderToOrderDto(order);
        }

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = order.getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not allowed");
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

        boolean isOwner = !orders.isEmpty()
            && orders.get(0).getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not allowed");
        }


        return orderMapper.orderListToOrderDtoList(orders);
    }

    @Override
    public OrderDto createOrder(OrderCreateDto createDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<Ticket> tickets = ticketRepository.findAllById(createDto.getTicketIds());

        Order order = new Order(user,
            tickets.stream().mapToLong(Ticket::getPriceFinalCents).sum());

        order.setTickets(tickets);

        Order saved = orderRepository.save(order);
        return orderMapper.orderToOrderDto(saved);
    }

    @Override
    public OrderDto updateOrder(long id, OrderUpdateDto updateDto) {

        Order order = orderRepository.findByIdWithTickets(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        List<Ticket> tickets = ticketRepository.findAllById(updateDto.getTicketIds());

        // Set new tickets
        order.setTickets(tickets);

        order.setTotalPriceCents(
            tickets.stream().mapToLong(Ticket::getPriceFinalCents).sum()
        );

        Order saved = orderRepository.save(order);
        return orderMapper.orderToOrderDto(saved);
    }
}
