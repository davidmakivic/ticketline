package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CancellationResultDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CancelTicketsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderMerchItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderMerchItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.MerchandiseVariant;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.OrderMerchItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseVariantRepository;
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
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final MerchandiseVariantRepository merchVariantRepository;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        UserRepository userRepository,
        TicketRepository ticketRepository,
        MerchandiseVariantRepository merchVariantRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.merchVariantRepository = merchVariantRepository;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        LOGGER.info("Fetching all orders");
        return orderRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public OrderDto getOrder(long id) {
        LOGGER.info("Fetching order with id={}", id);

        Order order = orderRepository.findByIdWithTickets(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return toDto(order);
        }

        String email = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = order.getUser() != null && order.getUser().getEmail() != null
            && order.getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not allowed");
        }

        return toDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByUser(Long userId) {
        LOGGER.info("Fetching all orders for user {}", userId);
        return orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public OrderDto createOrder(OrderCreateDto createDto) throws ValidationException, ConflictException {
        LOGGER.info("Creating order");
        LOGGER.debug("Payload: {}", createDto);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new NotFoundException("Not authenticated");
        }

        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<Long> ticketIds = createDto.getTicketIds();
        List<OrderMerchItemCreateDto> merchCreate = createDto.getMerchItems();

        boolean hasTickets = ticketIds != null && !ticketIds.isEmpty();
        boolean hasMerch = merchCreate != null && !merchCreate.isEmpty();

        if (!hasTickets && !hasMerch) {
            throw new ValidationException("No items provided",
                List.of("ticketIds or merchItems must not be empty"));
        }

        List<Ticket> tickets = new ArrayList<>();
        if (hasTickets) {
            tickets = ticketRepository.findAllById(ticketIds);

            if (tickets.size() != ticketIds.size()) {
                throw new NotFoundException("One or more tickets not found");
            }

            for (Ticket t : tickets) {
                if (t.getStatus() != TicketStatus.RESERVED) {
                    throw new ConflictException(
                        "Ticket not reserved",
                        List.of("Ticket " + t.getId() + " is " + t.getStatus())
                    );
                }
            }

            for (Ticket t : tickets) {
                t.setStatus(TicketStatus.PURCHASED);
            }
            ticketRepository.saveAll(tickets);
        }

        long ticketTotal = tickets.stream()
            .mapToLong(t -> t.getPriceFinalCents() == null ? 0 : t.getPriceFinalCents())
            .sum();

        long merchTotal = 0;
        List<OrderMerchItem> merchItems = new ArrayList<>();

        if (hasMerch) {
            for (OrderMerchItemCreateDto mi : merchCreate) {
                if (mi.getVariantId() == null || mi.getQuantity() == null || mi.getQuantity() <= 0) {
                    throw new ValidationException("Invalid merchandise item",
                        List.of("variantId must not be null and quantity must be > 0"));
                }

                MerchandiseVariant variant = merchVariantRepository.findById(mi.getVariantId())
                    .orElseThrow(() -> new NotFoundException("Merchandise variant not found"));

                Integer stock = variant.getQuantity();
                if (stock == null || stock < mi.getQuantity()) {
                    throw new ConflictException("Not enough stock",
                        List.of("variantId " + mi.getVariantId() + " has only " + stock));
                }

                variant.setQuantity(stock - mi.getQuantity());
                merchVariantRepository.save(variant);

                Integer priceCents = variant.getMerchandise().getPrice();
                long unitPriceCents = priceCents == null ? 0L : priceCents.longValue();

                merchTotal += unitPriceCents * mi.getQuantity();
                merchItems.add(new OrderMerchItem(null, variant, mi.getQuantity(), unitPriceCents));
            }
        }

        Order order = new Order(user, ticketTotal + merchTotal);
        order.setTickets(tickets);

        for (OrderMerchItem item : merchItems) {
            order.addMerchItem(item);
        }

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    public OrderDto updateOrder(long id, OrderUpdateDto updateDto) {
        LOGGER.info("Updating order with id={}", id);
        LOGGER.debug("Payload: {}", updateDto);

        Order order = orderRepository.findByIdWithTickets(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        List<Long> ticketIds = updateDto.getTicketIds();
        List<Ticket> tickets = new ArrayList<>();

        if (ticketIds != null && !ticketIds.isEmpty()) {
            tickets = ticketRepository.findAllById(ticketIds);
            order.setTickets(tickets);
        } else {
            order.setTickets(new ArrayList<>());
        }

        long ticketsSum = order.getTickets().stream()
            .mapToLong(t -> t.getPriceFinalCents() == null ? 0 : t.getPriceFinalCents())
            .sum();

        long merchSum = order.getMerchItems().stream()
            .mapToLong(mi -> (mi.getUnitPriceCents() == null ? 0 : mi.getUnitPriceCents()) * (mi.getQuantity() == null ? 0 : mi.getQuantity()))
            .sum();

        order.setTotalPriceCents(ticketsSum + merchSum);

        Order saved = orderRepository.save(order);
        return toDto(saved);
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

        boolean isOwner = order.getUser() != null && order.getUser().getEmail() != null
            && order.getUser().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not allowed");
        }

        var orderTicketIds = order.getTickets().stream().map(Ticket::getId).toList();
        for (Long tid : ticketIds) {
            if (!orderTicketIds.contains(tid)) {
                try {
                    throw new ConflictException("Ticket not in order",
                        List.of("Ticket " + tid + " not part of order " + orderId));
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
            if (t.getStatus() == TicketStatus.PURCHASED) {
                refund += (t.getPriceFinalCents() == null ? 0 : t.getPriceFinalCents());
                t.setStatus(TicketStatus.AVAILABLE);
                cancelled.add(t.getId());
            } else if (t.getStatus() == TicketStatus.AVAILABLE) {
                cancelled.add(t.getId());
            } else {
                try {
                    throw new ConflictException("Ticket not purchased",
                        List.of("Ticket " + t.getId() + " is " + t.getStatus()));
                } catch (ConflictException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        ticketRepository.saveAll(tickets);
        ticketRepository.detachFromOrder(ticketIds);

        long newTotal = Math.max(0, order.getTotalPriceCents() - refund);
        order.setTotalPriceCents(newTotal);
        orderRepository.save(order);

        return new CancellationResultDto(order.getId(), cancelled, refund, Instant.now());
    }

    private OrderDto toDto(Order o) {
        List<Long> ticketIds = o.getTickets().stream().map(Ticket::getId).toList();

        List<OrderMerchItemDto> merchDtos = o.getMerchItems().stream()
            .map(mi -> new OrderMerchItemDto(
                mi.getVariant().getId(),
                mi.getVariant().getMerchandise().getId(),
                mi.getVariant().getMerchandise().getName(),
                mi.getVariant().getSize(),
                mi.getQuantity(),
                mi.getUnitPriceCents()
            ))
            .toList();

        return new OrderDto(
            o.getId(),
            o.getUser().getUserId(),
            o.getTotalPriceCents(),
            o.getCreatedAt(),
            ticketIds,
            merchDtos
        );
    }
}
