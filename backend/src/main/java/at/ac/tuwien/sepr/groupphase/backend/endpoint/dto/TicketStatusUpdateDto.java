package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

public class TicketStatusUpdateDto {

    private TicketStatus status;

    public TicketStatusUpdateDto() {
    }

    public TicketStatusUpdateDto(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
