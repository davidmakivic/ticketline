package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;

@Profile("generateData")
@Component
public class TicketDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int TICKETS_PER_PERFORMANCE = 10;

    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public TicketDataGenerator(
        TicketRepository ticketRepository,
        PerformanceRepository performanceRepository,
        SeatRepository seatRepository,
        OrderRepository orderRepository,
        UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void generateTicketData() {
        if (!ticketRepository.findAll().isEmpty()) {
            LOGGER.debug("Tickets already generated");
            return;
        }

        List<Performance> performances = performanceRepository.findAll();
        if (performances.isEmpty()) {
            LOGGER.warn("No performances available – cannot generate tickets");
            return;
        }

        List<Seat> seats = seatRepository.findAll();
        List<ApplicationUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            LOGGER.warn("No users available – cannot generate orders for tickets");
            return;
        }

        Random random = new Random();

        LOGGER.debug("Generating {} tickets for each of {} performances",
            TICKETS_PER_PERFORMANCE, performances.size());

        for (Performance performance : performances) {
            for (int i = 0; i < TICKETS_PER_PERFORMANCE; i++) {
                ApplicationUser user = users.get(random.nextInt(users.size()));

                long finalPrice = performance.getBasePriceCents()
                    + random.nextInt(300);

                Order order = new Order(user, finalPrice);
                order = orderRepository.save(order);

                Ticket ticket = new Ticket();
                ticket.setPerformance(performance);

                if (!seats.isEmpty() && random.nextBoolean()) {
                    Seat seat = seats.get(random.nextInt(seats.size()));
                    ticket.setSeat(seat);
                } else {
                    ticket.setSeat(null);
                }

                ticket.setOrder(order);
                ticket.setPriceFinalCents(finalPrice);
                ticket.setStatus(TicketStatus.AVAILABLE);

                LOGGER.debug("Saving ticket {}", ticket);
                ticketRepository.save(ticket);
            }
        }
    }
}
