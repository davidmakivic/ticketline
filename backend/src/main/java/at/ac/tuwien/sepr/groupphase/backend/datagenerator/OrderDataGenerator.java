package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@DependsOn({"userDataGenerator", "ticketDataGenerator"})
@Component
public class OrderDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String DEMO_USER_EMAIL = "user@email.com";

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public OrderDataGenerator(OrderRepository orderRepository,
                              UserRepository userRepository,
                              TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @PostConstruct
    public void generateOrders() {
        if (!orderRepository.findAll().isEmpty()) {
            LOG.debug("orders already generated");
            return;
        }

        LOG.debug("Generating demo orders for user {}", DEMO_USER_EMAIL);

        ApplicationUser user = userRepository.findUserByEmail(DEMO_USER_EMAIL);
        if (user == null) {
            LOG.warn("Demo user {} not found — cannot generate demo orders", DEMO_USER_EMAIL);
            return;
        }
        List<Ticket> availableTickets = ticketRepository.findAll().stream()
            .filter(t -> t.getStatus() == TicketStatus.AVAILABLE)
            .toList();

        if (availableTickets.size() < 4) {
            LOG.warn("Not enough available tickets ({} found) — need at least 4", availableTickets.size());
            return;
        }

        int index = 0;

        Ticket t1 = availableTickets.get(index++);
        t1.setStatus(TicketStatus.PURCHASED);

        List<Ticket> order1Tickets = new ArrayList<>();
        order1Tickets.add(t1);

        long order1Total = t1.getPriceFinalCents();

        Order order1 = new Order(user, order1Total, 0);
        order1.setTickets(order1Tickets);

        orderRepository.save(order1);
        ticketRepository.save(t1);

        LOG.debug("Created first order (id={}) with 1 purchased ticket (id={})",
                  order1.getId(), t1.getId());

        List<Ticket> order2Tickets = new ArrayList<>();
        long order2Total = 0L;

        for (int i = 0; i < 3; i++) {
            Ticket t = availableTickets.get(index++);
            t.setStatus(TicketStatus.PURCHASED);
            order2Tickets.add(t);
            order2Total += t.getPriceFinalCents();
        }

        Order order2 = new Order(user, order2Total, 0);
        order2.setTickets(order2Tickets);

        orderRepository.save(order2);
        ticketRepository.saveAll(order2Tickets);

        LOG.debug("Created second order (id={}) with {} purchased tickets",
                  order2.getId(), order2Tickets.size());

        LOG.debug("Order generation complete");
    }
}
