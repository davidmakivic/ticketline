package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TicketGenerationService {
    /**
     * Generiert alle Tickets für eine Performance basierend auf den Seats der Hall.
     *
     * @param performance die Performance, für die Tickets erstellt werden sollen
     */
    void generateTicketsForPerformance(Performance performance);

    @Transactional
    void generateTicketsForPerformanceWithSeats(Performance performance, List<Seat> seats);
}
