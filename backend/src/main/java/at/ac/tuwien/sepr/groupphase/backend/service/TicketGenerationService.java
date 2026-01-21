package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;

public interface TicketGenerationService {
    /**
     * Generiert alle Tickets für eine Performance basierend auf den Seats der Hall.
     *
     * @param performance die Performance, für die Tickets erstellt werden sollen
     */
    void generateTicketsForPerformance(Performance performance);
}
