package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

public class TicketDto {

    private Long id;
    private Long performanceId;
    private Long seatId;

    private Integer seatRow;
    private Integer seatNumber;
    private String sectorName;

    private Long priceFinalCents;
    private TicketStatus status;
    private Long version;

    public TicketDto() {
    }

    public TicketDto(Long id, Long performanceId, Long seatId, Integer seatRow, Integer seatNumber, String sectorName,
                     Long priceFinalCents, TicketStatus status, Long version) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatId = seatId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.sectorName = sectorName;
        this.priceFinalCents = priceFinalCents;
        this.status = status;
        this.version = version;
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

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
