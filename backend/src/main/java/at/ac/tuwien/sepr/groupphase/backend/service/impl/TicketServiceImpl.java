package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final TicketGenerationServiceImpl ticketGenerationService;

    public TicketServiceImpl(
        TicketRepository ticketRepository,
        TicketMapper ticketMapper,
        PerformanceRepository performanceRepository,
        SeatRepository seatRepository, TicketGenerationServiceImpl ticketGenerationService
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.ticketGenerationService = ticketGenerationService;
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
    public TicketDto findById(Long id) {
        LOGGER.info("Fetching ticket with id={}", id);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ticket with id " + id + " not found"));

        Instant now = Instant.now();
        TicketStatus before = ticket.getStatus();
        Instant untilBefore = ticket.getReservedUntil();
        Long byBefore = ticket.getReservedByUserId();

        normalizeExpired(ticket, now);

        boolean changed =
            ticket.getStatus() != before
                || !Objects.equals(ticket.getReservedUntil(), untilBefore)
                || !Objects.equals(ticket.getReservedByUserId(), byBefore);

        if (changed) {
            ticket = ticketRepository.saveAndFlush(ticket);
        }

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
    public List<TicketDto> findByPerformanceId(Long performanceId) {
        LOGGER.info("Fetching tickets for performance {}", performanceId);

        // Prüfen, ob Tickets existieren, wenn nicht -> generieren
        if (!ticketRepository.existsByPerformanceId(performanceId)) {
            Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new NotFoundException("Performance not found"));
            ticketGenerationService.generateTicketsForPerformance(performance);
        }

        Instant now = Instant.now();
        List<Ticket> tickets = ticketRepository.findByPerformanceId(performanceId);

        boolean changed = false;

        for (Ticket t : tickets) {
            TicketStatus before = t.getStatus();
            Instant untilBefore = t.getReservedUntil();
            Long byBefore = t.getReservedByUserId();

            normalizeExpired(t, now);

            if (t.getStatus() != before
                || !Objects.equals(t.getReservedUntil(), untilBefore)
                || !Objects.equals(t.getReservedByUserId(), byBefore)) {
                changed = true;
            }
        }

        if (changed) {
            ticketRepository.saveAllAndFlush(tickets);
        }

        return ticketMapper.ticketListToTicketDtoList(tickets);
    }

    @Override
    @Transactional
    public TicketDto hold(Long ticketId, Long userId) throws ConflictException {
        Instant now = Instant.now();
        Instant until = now.plus(Duration.ofMinutes(10));

        int updated = ticketRepository.holdAtomically(ticketId, userId, until, now);
        if (updated == 0) {
            throw new ConflictException("Ticket not available", List.of("Ticket " + ticketId + " is already held/purchased by another user (or not available).")); // mapped to 409
        }
        Ticket t = ticketRepository.findById(ticketId).orElseThrow();
        return ticketMapper.ticketToTicketDto(t);
    }

    @Override
    @Transactional
    public TicketDto release(Long ticketId, Long userId) throws ConflictException {
        Instant now = Instant.now();

        Ticket t = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new NotFoundException("Ticket " + ticketId + " not found"));

        normalizeExpired(t, now);
        if (t.getStatus() == TicketStatus.AVAILABLE) {
            ticketRepository.saveAndFlush(t);
            return ticketMapper.ticketToTicketDto(t);
        }

        if (t.getStatus() == TicketStatus.RESERVED && !Objects.equals(t.getReservedByUserId(), userId)) {
            throw new ConflictException(
                "Ticket not held by user",
                List.of("Ticket " + ticketId + " is held by another user.")
            );
        }

        int updated = ticketRepository.releaseHoldOwned(ticketId, userId, Instant.now());
        if (updated == 0) {
            throw new ConflictException(
                "Ticket not held by user",
                List.of("Ticket " + ticketId + " is not currently held by the current user.")
            );
        }

        Ticket after = ticketRepository.findById(ticketId).orElseThrow();
        return ticketMapper.ticketToTicketDto(after);
    }



    private void normalizeExpired(Ticket t, Instant now) {
        if (t.getStatus() == TicketStatus.RESERVED
            && t.getReservedUntil() != null
            && !t.getReservedUntil().isAfter(now)) {

            t.setStatus(TicketStatus.AVAILABLE);
            t.setReservedUntil(null);
            t.setReservedByUserId(null);
        }
    }
}
