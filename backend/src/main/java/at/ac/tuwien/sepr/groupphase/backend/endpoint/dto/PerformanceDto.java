package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public class PerformanceDto {
    private Long id;
    private Long eventId;
    private Long hallId;
    private Date startTime;
    private Date endTime;
    private Integer basePriceCents;

    public PerformanceDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
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

    public void setEndTime(Date endtime) {
        this.endTime = endtime;
    }

    public Integer getBasePriceCents() {
        return basePriceCents;
    }

    public void setBasePriceCents(Integer basePriceCents) {
        this.basePriceCents = basePriceCents;
    }
}
