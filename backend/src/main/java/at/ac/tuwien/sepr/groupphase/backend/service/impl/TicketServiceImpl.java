package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
        LOGGER.info("Creating ticket for performance {}", dto.getPerformanceId());
        LOGGER.debug("Payload: {}", dto);
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
        LOGGER.info("Fetching ticket with id={}", id);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ticket with id " + id + " not found"));

        return ticketMapper.ticketToTicketDto(ticket);
    }

    @Override
    public TicketDto update(Long id, TicketDto dto) {
        LOGGER.info("Updating ticket with id={}", id);
        LOGGER.debug("Payload: {}", dto);
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
    public TicketDto updateStatus(Long id, TicketStatus status, Long version) {
        LOGGER.info("Updating ticket status for id={} to {}", id, status);
        LOGGER.debug("Version={}", version);

        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ticket with id " + id + " not found"));

        ticket.setVersion(version);
        ticket.setStatus(status);

        Ticket saved = ticketRepository.saveAndFlush(ticket);
        return ticketMapper.ticketToTicketDto(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> findByPerformanceId(Long performanceId) {
        LOGGER.info("Fetching tickets for performance {}", performanceId);
        return ticketMapper.ticketListToTicketDtoList(
            ticketRepository.findByPerformance_Id(performanceId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> findAll() {
        return ticketMapper.ticketListToTicketDtoList(ticketRepository.findAll());
    }

}
