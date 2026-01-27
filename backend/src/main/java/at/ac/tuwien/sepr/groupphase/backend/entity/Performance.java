package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import java.util.Date;

@Entity
@Table(name = "performances", indexes = {
    @Index(name = "idx_performance_event_id", columnList = "event_id"),
    @Index(name = "idx_performance_hall_id", columnList = "hall_id"),
    @Index(name = "idx_performance_start_time", columnList = "start_time"),
    @Index(name = "idx_performance_end_time", columnList = "end_time"),
    @Index(name = "idx_performance_event_start", columnList = "event_id, start_time")
})

public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "base_price_cents")
    private Long basePriceCents;

    public Performance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getBasePriceCents() {
        return basePriceCents;
    }

    public void setBasePriceCents(Long basePriceCents) {
        this.basePriceCents = basePriceCents;
    }
}
