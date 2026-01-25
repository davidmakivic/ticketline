package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

import java.util.List;

/**
 * Service interface for managing tickets.
 * Provides methods for creating, retrieving, updating, and deleting tickets,
 * as well as querying tickets by associated performance.
 */
public interface TicketService {

    /**
     * Creates a new ticket.
     *
     * @param ticketDto the ticket data used to create the ticket
     * @return the created TicketDto
     */
    TicketDto create(TicketDto ticketDto);

    /**
     * Retrieves a ticket by its ID.
     *
     * @param id the ID of the ticket
     * @return the corresponding TicketDto
     * @throws at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException if no ticket exists with the given ID
     */
    TicketDto findById(Long id);


    /**
     * Updates an existing ticket.
     *
     * @param id        the ID of the ticket to update
     * @param ticketDto the updated ticket data
     * @return the updated TicketDto
     */
    TicketDto update(Long id, TicketDto ticketDto);

    /**
     * Retrieves all tickets associated with a given performance.
     *
     * @param performanceId the ID of the performance
     * @return list of TicketDto belonging to that performance
     */
    List<TicketDto> findByPerformanceId(Long performanceId);

    TicketDto release(Long id, Long userId) throws ConflictException;

    TicketDto hold(Long id, Long userId) throws ConflictException;
}
