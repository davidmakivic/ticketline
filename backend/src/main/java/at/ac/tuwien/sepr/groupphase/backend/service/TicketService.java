package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

import java.util.List;

public interface TicketService {

    TicketDto create(TicketDto ticketDto);

    TicketDto findById(Long id);

    List<TicketDto> findAll();

    TicketDto update(Long id, TicketDto ticketDto);

    TicketDto updateStatus(Long id, TicketStatus status);

    void delete(Long id);
}
