package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public TicketDto create(TicketDto ticketDto) {
        Ticket ticket = ticketMapper.ticketDtoToTicket(ticketDto);
        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.ticketToTicketDto(saved);
    }

    @Override
    public TicketDto findById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ticket with id " + id + " not found"));
        return ticketMapper.ticketToTicketDto(ticket);
    }

    @Override
    public List<TicketDto> findAll() {
        return ticketMapper.ticketListToTicketDtoList(ticketRepository.findAll());
    }

    @Override
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket with id " + id + " not found");
        }
        ticketRepository.deleteById(id);
    }
}
