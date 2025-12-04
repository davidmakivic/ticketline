package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

public class TicketDto {

    private Long id;
    private Long performanceId;
    private Long seatId;
    private Long orderId;
    private Long priceFinalCents;
    private TicketStatus status;

    public TicketDto() {
    }

    public TicketDto(Long id, Long performanceId, Long seatId, Long orderId, Long priceFinalCents, TicketStatus status) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatId = seatId;
        this.orderId = orderId;
        this.priceFinalCents = priceFinalCents;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPriceFinalCents() {
        return priceFinalCents;
    }

    public void setPriceFinalCents(Long priceFinalCents) {
        this.priceFinalCents = priceFinalCents;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
