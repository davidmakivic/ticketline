package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface PerformanceService {

    PerformanceDto create(PerformanceDto dto);

    PerformanceDto update(Long id, PerformanceDto dto);

    PerformanceDto findById(Long id);

    Page<PerformanceDto> findAll(int page, int size);

    Page<PerformanceDto> findByAdvancedFilters(
        String title, String artist, String location,
        EventType eventType, Date startDate, Integer durationMinutes,
        int page, int size);
}
