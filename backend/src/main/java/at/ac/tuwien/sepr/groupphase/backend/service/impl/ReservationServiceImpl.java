package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ReservationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  TicketRepository ticketRepository,
                                  UserRepository userRepository,
                                  ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationDto create(ReservationCreateDto dto) throws ConflictException, ValidationException {
        LOGGER.info("Creating reservation");
        LOGGER.debug("Payload: {}", dto);

        if (dto.getTicketIds() == null || dto.getTicketIds().isEmpty()) {
            throw new ValidationException("No tickets provided", List.of("ticketIds must not be empty"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<Ticket> tickets = ticketRepository.findAllById(dto.getTicketIds());
        if (tickets.size() != dto.getTicketIds().size()) {
            throw new NotFoundException("One or more tickets not found");
        }

        for (Ticket t : tickets) {
            if (t.getStatus() != TicketStatus.AVAILABLE) {
                throw new ConflictException(
                    "Ticket not available",
                    List.of("Ticket " + t.getId() + " is " + t.getStatus())
                );
            }
        }

        for (Ticket t : tickets) {
            t.setStatus(TicketStatus.RESERVED);
        }
        ticketRepository.saveAll(tickets);

        String reservationNumber = generateReservationNumber();

        Reservation reservation = new Reservation(user, reservationNumber);
        reservation.setTickets(tickets);

        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.reservationToReservationDto(saved);
    }

    private String generateReservationNumber() {
        return "R-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
