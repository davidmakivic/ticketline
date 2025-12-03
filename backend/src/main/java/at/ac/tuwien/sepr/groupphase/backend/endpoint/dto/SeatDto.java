package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import ch.qos.logback.core.joran.spi.DefaultClass;

public class SeatDto {

    private Long id;
    private int rowNumber;
    private int seatNumber;
    private Long sectorId;


    public SeatDto(int rowNumber, int seatNumber, Long sectorId) {
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.sectorId = sectorId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
