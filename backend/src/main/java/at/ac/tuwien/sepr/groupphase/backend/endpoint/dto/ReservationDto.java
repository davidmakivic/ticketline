package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;
import java.util.List;

public class ReservationDto {

    private final Long id;
    private final Long userId;
    private final String reservationNumber;
    private final Instant createdAt;
    private final List<Long> ticketIds;

    public ReservationDto(Long id, Long userId, String reservationNumber, Instant createdAt, List<Long> ticketIds) {
        this.id = id;
        this.userId = userId;
        this.reservationNumber = reservationNumber;
        this.createdAt = createdAt;
        this.ticketIds = ticketIds;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Long> getTicketIds() {
        return ticketIds;
    }
}
