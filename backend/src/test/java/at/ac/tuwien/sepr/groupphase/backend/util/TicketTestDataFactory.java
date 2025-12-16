package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;

public class TicketTestDataFactory {

    public static TicketDto create(Long performanceId) {
        TicketDto dto = new TicketDto();
        dto.setPerformanceId(performanceId);
        dto.setSeatId(null);
        dto.setPriceFinalCents(2500L);
        dto.setStatus(TicketStatus.AVAILABLE);
        return dto;
    }
}
