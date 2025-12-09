package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    public TicketServiceImpl(
        TicketRepository ticketRepository,
        TicketMapper ticketMapper,
        PerformanceRepository performanceRepository,
        SeatRepository seatRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public TicketDto create(TicketDto dto) {
        Ticket ticket = ticketMapper.ticketDtoToTicket(dto);

        ticket.setPerformance(performanceRepository.getReferenceById(dto.getPerformanceId()));

        if (dto.getSeatId() != null) {
            ticket.setSeat(seatRepository.getReferenceById(dto.getSeatId()));
        } else {
            ticket.setSeat(null);
        }

        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.ticketToTicketDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDto findById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ticket with id " + id + " not found"));

        return ticketMapper.ticketToTicketDto(ticket);
    }

    @Override
    public TicketDto update(Long id, TicketDto dto) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ticket " + id + " not found"));

        ticket.setPerformance(performanceRepository.getReferenceById(dto.getPerformanceId()));

        if (dto.getSeatId() != null) {
            ticket.setSeat(seatRepository.getReferenceById(dto.getSeatId()));
        } else {
            ticket.setSeat(null);
        }

        ticket.setPriceFinalCents(dto.getPriceFinalCents());
        ticket.setStatus(dto.getStatus());

        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.ticketToTicketDto(saved);
    }

    @Override
    public TicketDto updateStatus(Long id, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ticket with id " + id + " not found"));

        ticket.setStatus(status);
        Ticket saved = ticketRepository.save(ticket);

        return ticketMapper.ticketToTicketDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> findByPerformanceId(Long performanceId) {
        return ticketMapper.ticketListToTicketDtoList(
            ticketRepository.findByPerformance_Id(performanceId)
        );
    }

}
