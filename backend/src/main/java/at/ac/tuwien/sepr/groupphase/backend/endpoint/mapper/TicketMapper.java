package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {


    @Mapping(target = "performanceId", source = "performance.id")
    @Mapping(target = "seatId", source = "seat.id")
    @Mapping(target = "orderId", source = "order.id")
    TicketDto ticketToTicketDto(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "performance", ignore = true)
    @Mapping(target = "seat", ignore = true)
    @Mapping(target = "order", ignore = true)
    Ticket ticketDtoToTicket(TicketDto ticketDto);

    List<TicketDto> ticketListToTicketDtoList(List<Ticket> tickets);
}
