package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;

import java.util.List;

public interface TicketService {

    TicketDto create(TicketDto ticketDto);

    TicketDto findById(Long id);

    List<TicketDto> findAll();

    void delete(Long id);
}
