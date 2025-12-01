package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private int id;

    @Column(name = "performance_id", nullable = false)
    private int performanceId;

    @Column(name = "seat_id")
    private int seatId;

    @Column(name = "order_id", nullable = false)
    private int orderId;

    @Column(name = "price_final_cents")
    private int priceFinalCents;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TicketStatus status;

    public Ticket() {}

    public Ticket(int performanceId, int seatId, int orderId, Integer priceFinalCents, TicketStatus status) {
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
