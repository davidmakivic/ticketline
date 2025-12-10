package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

public class TicketStatusUpdateDto {

    private TicketStatus status;
    private Long version;

    public TicketStatusUpdateDto() {
    }

    public TicketStatusUpdateDto(TicketStatus status, Long version) {
        this.status = status;
        this.version = version;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
