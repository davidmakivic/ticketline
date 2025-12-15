package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CancellationResultDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
        LOGGER.info("Fetching all orders");
        return orderMapper.orderListToOrderDtoList(
            orderRepository.findAllByOrderByCreatedAtDesc()
        );
    }

    @Override
    public OrderDto getOrder(long id) {
        LOGGER.info("Fetching order with id={}", id);
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            ApplicationUser me = userRepository.findUserByEmail(email);
            if (me == null || !me.getUserId().equals(userId)) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Not allowed");
            }
        }

        List<Order> orders = orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);
        return orderMapper.orderListToOrderDtoList(orders);
    }


    @Override
    public OrderDto createOrder(OrderCreateDto createDto) throws ValidationException, ConflictException {

        LOGGER.info("Creating order");
        LOGGER.debug("Payload: {}", createDto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<Long> ids = createDto.getTicketIds();
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("No tickets provided", List.of("ticketIds must not be empty"));
        }

        List<Ticket> tickets = ticketRepository.findAllById(ids);
        if (tickets.size() != ids.size()) {
            throw new NotFoundException("One or more tickets not found");
        }

        long total = 0;
        for (Ticket t : tickets) {
            if (t.getStatus() != TicketStatus.RESERVED) {
                throw new ConflictException(
                    "Ticket not reserved",
                    List.of("Ticket " + t.getId() + " is " + t.getStatus())
                );
            }
            total += (t.getPriceFinalCents() == null ? 0 : t.getPriceFinalCents());
        }

        for (Ticket t : tickets) {
            t.setStatus(TicketStatus.PURCHASED);
        }
        ticketRepository.saveAll(tickets);

        Order order = new Order(user,
            tickets.stream().mapToLong(Ticket::getPriceFinalCents).sum());

        order.setTickets(tickets);

        Order saved = orderRepository.save(order);
        return orderMapper.orderToOrderDto(saved);
    }

    @Override
    public OrderDto updateOrder(long id, OrderUpdateDto updateDto) {
        LOGGER.info("Updating order with id={}", id);
        LOGGER.debug("Payload: {}", updateDto);
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

    @Override
    @Transactional
    public CancellationResultDto cancelTickets(long orderId, List<Long> ticketIds) {

        LOGGER.info("Cancelling tickets for orderId={}", orderId);

        if (ticketIds == null || ticketIds.isEmpty()) {
            try {
                throw new ValidationException("No tickets provided", List.of("ticketIds must not be empty"));
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }

        Order order = orderRepository.findByIdWithTickets(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = order.getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not allowed");
        }

        var orderTicketIds = order.getTickets().stream().map(Ticket::getId).toList();
        for (Long tid : ticketIds) {
            if (!orderTicketIds.contains(tid)) {
                try {
                    throw new ConflictException("Ticket not in order", List.of("Ticket " + tid + " not part of order " + orderId));
                } catch (ConflictException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        if (tickets.size() != ticketIds.size()) {
            throw new NotFoundException("One or more tickets not found");
        }

        long refund = 0;
        List<Long> cancelled = new ArrayList<>();

        for (Ticket t : tickets) {
            if (t.getStatus() != TicketStatus.PURCHASED) {
                try {
                    throw new ConflictException("Ticket not purchased", List.of("Ticket " + t.getId() + " is " + t.getStatus()));
                } catch (ConflictException e) {
                    throw new RuntimeException(e);
                }
            }

            refund += (t.getPriceFinalCents() == null ? 0 : t.getPriceFinalCents());

            t.setStatus(TicketStatus.AVAILABLE);
            cancelled.add(t.getId());
        }

        ticketRepository.saveAll(tickets);

        ticketRepository.detachFromOrder(ticketIds);

        long newTotal = Math.max(0, order.getTotalPriceCents() - refund);
        order.setTotalPriceCents(newTotal);
        orderRepository.save(order);

        return new CancellationResultDto(order.getId(), cancelled, refund, Instant.now());
    }

    @Override
    public List<OrderDto> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<Order> orders = orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
        return orderMapper.orderListToOrderDtoList(orders);
    }


}
