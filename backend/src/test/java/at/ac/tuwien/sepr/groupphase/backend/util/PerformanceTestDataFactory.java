package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;

import java.util.Date;

public class PerformanceTestDataFactory {

    public static PerformanceDto create(Long eventId, Long hallId) {
        PerformanceDto dto = new PerformanceDto();
        dto.setEventId(eventId);
        dto.setHallId(hallId);
        dto.setBasePriceCents(2500);
        dto.setStartTime(new Date(System.currentTimeMillis()));
        dto.setEndTime(new Date(System.currentTimeMillis() + 90 * 60 * 1000)); // +90min
        return dto;
    }
}
