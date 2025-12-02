package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

public class TicketDto {

    private int id;
    private int performanceId;
    private int seatId;
    private int orderId;
    private int priceFinalCents;
    private TicketStatus status;

    public TicketDto() {
    }

    public TicketDto(int id, int performanceId, int seatId, int orderId, int priceFinalCents, TicketStatus status) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatId = seatId;
        this.orderId = orderId;
        this.priceFinalCents = priceFinalCents;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(int performanceId) {
        this.performanceId = performanceId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPriceFinalCents() {
        return priceFinalCents;
    }

    public void setPriceFinalCents(int priceFinalCents) {
        this.priceFinalCents = priceFinalCents;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
