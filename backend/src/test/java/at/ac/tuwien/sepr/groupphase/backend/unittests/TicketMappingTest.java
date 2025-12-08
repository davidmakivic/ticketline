package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TicketMappingTest {

    @Autowired
    private TicketMapper ticketMapper;

    private Ticket buildTicket() {
        Performance performance = new Performance();
        performance.setId(10L);

        Seat seat = new Seat();
        seat.setId(20L);

        Order order = new Order();
        order.setId(30L);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPerformance(performance);
        ticket.setSeat(seat);
        ticket.setPriceFinalCents(3500L);
        ticket.setStatus(TicketStatus.AVAILABLE);

        return ticket;
    }

    @Test
    void givenTicket_whenMapToDto_thenAllFieldsMapped() {
        Ticket ticket = buildTicket();

        TicketDto dto = ticketMapper.ticketToTicketDto(ticket);

        assertAll(
            () -> assertEquals(ticket.getId(), dto.getId()),
            () -> assertEquals(ticket.getPerformance().getId(), dto.getPerformanceId()),
            () -> assertEquals(ticket.getSeat().getId(), dto.getSeatId()),
            () -> assertEquals(ticket.getPriceFinalCents(), dto.getPriceFinalCents()),
            () -> assertEquals(ticket.getStatus(), dto.getStatus())
        );
    }

    @Test
    void givenDto_whenMapToEntity_thenSimpleFieldsMappedAndRelationsIgnored() {
        TicketDto dto = new TicketDto();
        dto.setId(5L);
        dto.setPerformanceId(10L);
        dto.setSeatId(20L);
        dto.setPriceFinalCents(4000L);
        dto.setStatus(TicketStatus.AVAILABLE);

        Ticket entity = ticketMapper.ticketDtoToTicket(dto);

        assertAll(
            () -> assertNull(entity.getId(), "id should be null because it is ignored"),
            () -> assertNull(entity.getPerformance(), "performance should be null because it is ignored"),
            () -> assertNull(entity.getSeat(), "seat should be null because it is ignored"),
            () -> assertEquals(dto.getPriceFinalCents(), entity.getPriceFinalCents()),
            () -> assertEquals(dto.getStatus(), entity.getStatus())
        );
    }

    @Test
    void givenListOfTickets_whenMapToDtoList_thenSizeAndContentMatch() {
        Ticket t1 = buildTicket();
        Ticket t2 = buildTicket();

        List<TicketDto> dtos = ticketMapper.ticketListToTicketDtoList(List.of(t1, t2));

        assertEquals(2, dtos.size());
        TicketDto first = dtos.get(0);

        assertAll(
            () -> assertEquals(t1.getId(), first.getId()),
            () -> assertEquals(t1.getPerformance().getId(), first.getPerformanceId()),
            () -> assertEquals(t1.getSeat().getId(), first.getSeatId())
        );
    }
}
