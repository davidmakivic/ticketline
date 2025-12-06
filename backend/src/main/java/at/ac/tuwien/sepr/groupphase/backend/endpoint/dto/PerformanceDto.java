package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public class PerformanceDto {
    private Long performanceId;
    private Long eventId;
    private Long hallId;
    private Date startTime;
    private Date endTime;
    private Integer basePriceCents;

    public PerformanceDto() {
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
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

    public void setStartTime(Date starttime) {
        this.startTime = starttime;
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
