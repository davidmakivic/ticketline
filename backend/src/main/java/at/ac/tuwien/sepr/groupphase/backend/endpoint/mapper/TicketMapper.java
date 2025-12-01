package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketDto ticketToTicketDto(Ticket ticket);

    Ticket ticketDtoToTicket(TicketDto ticketDto);

    List<TicketDto> ticketListToTicketDtoList(List<Ticket> tickets);
}
