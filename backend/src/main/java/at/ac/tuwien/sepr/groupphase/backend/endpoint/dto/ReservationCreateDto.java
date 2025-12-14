package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class ReservationCreateDto {

    private List<Long> ticketIds;

    public ReservationCreateDto() {
    }

    public ReservationCreateDto(List<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public List<Long> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(List<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }
}
