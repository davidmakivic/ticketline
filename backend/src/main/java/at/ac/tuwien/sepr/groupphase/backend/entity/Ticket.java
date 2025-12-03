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
    private Long id;

    @Column(name = "performance_id", nullable = false)
    private Long performanceId;

    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "price_final_cents")
    private Long priceFinalCents;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TicketStatus status;

    public Ticket() {}

    public Ticket(Long performanceId, Long seatId, Long orderId, Long priceFinalCents, TicketStatus status) {
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
