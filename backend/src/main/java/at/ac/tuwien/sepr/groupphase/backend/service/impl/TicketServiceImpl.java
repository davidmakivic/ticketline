package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;

    public TicketServiceImpl(
        TicketRepository ticketRepository,
        TicketMapper ticketMapper,
        PerformanceRepository performanceRepository,
        SeatRepository seatRepository,
        OrderRepository orderRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public TicketDto create(TicketDto ticketDto) {
        Ticket ticket = ticketMapper.ticketDtoToTicket(ticketDto);


        ticket.setPerformance(
            performanceRepository.getReferenceById(ticketDto.getPerformanceId())
        );


        if (ticketDto.getSeatId() != null) {
            ticket.setSeat(
                seatRepository.getReferenceById(ticketDto.getSeatId())
            );
        }

        ticket.setOrder(
            orderRepository.getReferenceById(ticketDto.getOrderId())
        );

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
    public TicketDto update(Long id, TicketDto ticketDto) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ticket with id " + id + " not found"));

        /*
        if (ticketDto.getPerformanceId() != null) {
            ticket.setPerformance(
                performanceRepository.getReferenceById(ticketDto.getPerformanceId()));
        }
        */

        if (ticketDto.getSeatId() != null) {
            ticket.setSeat(seatRepository.getReferenceById(ticketDto.getSeatId()));
        } else {
            ticket.setSeat(null);
        }

        if (ticketDto.getOrderId() != null) {
            ticket.setOrder(orderRepository.getReferenceById(ticketDto.getOrderId()));
        }

        ticket.setPriceFinalCents(ticketDto.getPriceFinalCents());
        ticket.setStatus(ticketDto.getStatus());

        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.ticketToTicketDto(saved);
    }

    @Override
    public TicketDto updateStatus(Long id, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ticket with id " + id + " not found"));

        ticket.setStatus(status);
        Ticket saved = ticketRepository.save(ticket);

        return ticketMapper.ticketToTicketDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket with id " + id + " not found");
        }
        ticketRepository.deleteById(id);
    }
}
