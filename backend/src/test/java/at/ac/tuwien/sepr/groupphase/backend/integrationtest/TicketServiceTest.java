package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Performance performance;
    private Order order;

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAll();
        orderRepository.deleteAll();
        performanceRepository.deleteAll();
        eventRepository.deleteAll();
        hallRepository.deleteAll();
        venueRepository.deleteAll();

        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue = venueRepository.save(venue);

        Hall hall = new Hall();
        hall.setName("Main Hall");
        hall.setVenue(venue);
        hall = hallRepository.save(hall);

        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test description");
        event.setCategory(EventType.CONCERT);
        event.setDurationMinutes(90);
        event = eventRepository.save(event);

        performance = new Performance();
        performance.setEvent(event);
        performance.setHall(hall);
        performance.setBasePriceCents(2000L);
        performance = performanceRepository.save(performance);

        ApplicationUser user = userRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No user found for TicketServiceTest"));

        order = new Order();
        order.setUser(user);
        order.setTotalPriceCents(0L);
        order = orderRepository.save(order);
    }

    private TicketDto buildDto(Long performanceId, Long orderId, Long priceCents, TicketStatus status) {
        TicketDto dto = new TicketDto();
        dto.setPerformanceId(performanceId);
        dto.setOrderId(orderId);
        dto.setSeatId(null);
        dto.setPriceFinalCents(priceCents);
        dto.setStatus(status);
        return dto;
    }

    @Transactional
    @Test
    void testCreateTicket() {
        TicketDto dto = buildDto(performance.getId(), order.getId(), 2500L, TicketStatus.AVAILABLE);

        TicketDto saved = ticketService.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPerformanceId()).isEqualTo(performance.getId());
        assertThat(saved.getOrderId()).isEqualTo(order.getId());
        assertThat(saved.getPriceFinalCents()).isEqualTo(2500L);
        assertThat(saved.getStatus()).isEqualTo(TicketStatus.AVAILABLE);

        assertThat(ticketRepository.findAll()).hasSize(1);
    }

    @Transactional
    @Test
    void testUpdateTicket() {
        TicketDto dto = buildDto(performance.getId(), order.getId(), 2500L, TicketStatus.AVAILABLE);
        TicketDto created = ticketService.create(dto);
        Long id = created.getId();

        ApplicationUser user = userRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No user found for TicketServiceTest"));

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotalPriceCents(0L);
        newOrder = orderRepository.save(newOrder);

        TicketDto updateDto = new TicketDto();
        updateDto.setPerformanceId(performance.getId());
        updateDto.setOrderId(newOrder.getId());
        updateDto.setSeatId(null);
        updateDto.setPriceFinalCents(3000L);
        updateDto.setStatus(TicketStatus.PURCHASED);

        TicketDto updated = ticketService.update(id, updateDto);

        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getPerformanceId()).isEqualTo(performance.getId());
        assertThat(updated.getOrderId()).isEqualTo(newOrder.getId());
        assertThat(updated.getPriceFinalCents()).isEqualTo(3000L);
        assertThat(updated.getStatus()).isEqualTo(TicketStatus.PURCHASED);
    }

    @Transactional
    @Test
    void testUpdateTicketNotFound() {
        TicketDto dto = buildDto(performance.getId(), order.getId(), 2000L, TicketStatus.AVAILABLE);

        assertThatThrownBy(() -> ticketService.update(999999L, dto))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAllTickets() {
        TicketDto dto1 = buildDto(performance.getId(), order.getId(), 1500L, TicketStatus.AVAILABLE);
        TicketDto dto2 = buildDto(performance.getId(), order.getId(), 2500L, TicketStatus.AVAILABLE);

        ticketService.create(dto1);
        ticketService.create(dto2);

        List<TicketDto> result = ticketService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testFindByIdNotFound() {
        assertThatThrownBy(() -> ticketService.findById(999999L))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testDeleteTicket() {
        TicketDto dto = buildDto(performance.getId(), order.getId(), 2200L, TicketStatus.AVAILABLE);
        TicketDto saved = ticketService.create(dto);
        Long id = saved.getId();

        assertThat(ticketService.findById(id)).isNotNull();

        ticketService.delete(id);

        assertThatThrownBy(() -> ticketService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testDeleteTicketNotFound() {
        assertThatThrownBy(() -> ticketService.delete(999999L))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testUpdateTicketStatus() {
        TicketDto dto = buildDto(performance.getId(), order.getId(), 1800L, TicketStatus.AVAILABLE);
        TicketDto saved = ticketService.create(dto);
        Long id = saved.getId();

        TicketDto updated = ticketService.updateStatus(id, TicketStatus.CANCELLED);

        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }
}
