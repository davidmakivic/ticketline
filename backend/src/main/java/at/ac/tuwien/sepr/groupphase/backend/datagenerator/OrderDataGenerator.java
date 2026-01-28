package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@DependsOn({"userDataGenerator", "ticketDataGenerator"})
@Component
@org.springframework.core.annotation.Order(200)
public class OrderDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String DEMO_USER_EMAIL = "user@email.com";
    private static final int TARGET_ORDERS = 15;

    private static final int PERFORMANCE_CANDIDATES = 60;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;

    public OrderDataGenerator(OrderRepository orderRepository,
                              UserRepository userRepository,
                              TicketRepository ticketRepository,
                              PerformanceRepository performanceRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
    }

    @PostConstruct
    public void generateOrders() {
        long existingOrders = orderRepository.count();
        if (existingOrders >= TARGET_ORDERS) {
            LOG.debug("Orders already generated ({} >= {})", existingOrders, TARGET_ORDERS);
            return;
        }

        ApplicationUser user = userRepository.findUserByEmail(DEMO_USER_EMAIL);
        if (user == null) {
            LOG.warn("Demo user {} not found — cannot generate demo orders", DEMO_USER_EMAIL);
            return;
        }

        int ordersToCreate = (int) Math.max(0, TARGET_ORDERS - existingOrders);
        int created = 0;


        Pageable page = PageRequest.of(0, PERFORMANCE_CANDIDATES, Sort.by("id").ascending());
        List<Performance> performances = performanceRepository.findAllIds(page);

        if (performances.isEmpty()) {
            LOG.warn("No performances found — cannot generate orders");
            return;
        }

        for (int performanceIndex = 0; performanceIndex < performances.size() && created < ordersToCreate; performanceIndex++) {


            Long performanceId = performances.get(performanceIndex).getId();
            if (performanceId == null) {
                continue;
            }

            List<Ticket> performanceTickets = ticketRepository.findByPerformanceId(performanceId).stream()
                .filter(t -> t.getStatus() == TicketStatus.AVAILABLE)
                .toList();

            int desiredTickets = ((created + 1) % 3 == 0) ? 3 : 2;

            if (performanceTickets.size() < desiredTickets) {
                continue;
            }

            List<Ticket> orderTickets = new ArrayList<>(desiredTickets);
            long total = 0L;

            for (int i = 0; i < desiredTickets; i++) {
                Ticket t = performanceTickets.get(i);
                t.setStatus(TicketStatus.PURCHASED);
                orderTickets.add(t);

                Long price = t.getPriceFinalCents();
                total += (price == null ? 0L : price);
            }

            Order order = new Order(user, total, 0);
            order.setTickets(orderTickets);

            Order saved = orderRepository.save(order);
            ticketRepository.saveAll(orderTickets);

            created++;
            LOG.debug("Created order {} (id={}) for performance {} with {} tickets (total={} cents)",
                created, saved.getId(), performanceId, orderTickets.size(), total);
        }

        if (created < ordersToCreate) {
            List<Ticket> remainingTickets = ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() == TicketStatus.AVAILABLE)
                .toList();

            int idx = 0;
            while (created < ordersToCreate && idx < remainingTickets.size()) {

                int desiredTickets = ((created + 1) % 3 == 0) ? 3 : 2;

                List<Ticket> orderTickets = new ArrayList<>(desiredTickets);
                long total = 0L;

                for (int i = 0; i < desiredTickets && idx < remainingTickets.size(); i++) {
                    Ticket t = remainingTickets.get(idx++);
                    t.setStatus(TicketStatus.PURCHASED);
                    orderTickets.add(t);

                    Long price = t.getPriceFinalCents();
                    total += (price == null ? 0L : price);
                }

                if (orderTickets.isEmpty()) {
                    break;
                }

                Order order = new Order(user, total, 0);
                order.setTickets(orderTickets);

                Order saved = orderRepository.save(order);
                ticketRepository.saveAll(orderTickets);

                created++;
                LOG.debug("Created fallback order {} (id={}) with {} tickets (total={} cents)",
                    created, saved.getId(), orderTickets.size(), total);
            }
        }

        LOG.debug("Order generation complete. created={}, existingBefore={}", created, existingOrders);
    }
}