package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public class PerformanceDto {
    private Long performanceId;
    private Long eventId;
    private Long hallId;
    private Date starttime;
    private Date endtime;
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

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public Integer getbasePriceCents() {
        return basePriceCents;
    }

    public void setbasePriceCents(Integer basePriceCents) {
        this.basePriceCents = basePriceCents;
    }
}
