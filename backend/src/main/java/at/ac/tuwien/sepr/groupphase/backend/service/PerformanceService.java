package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;

import java.util.List;

public interface PerformanceService {

    PerformanceDto create(PerformanceDto dto);

    PerformanceDto update(Long id, PerformanceDto dto);

    PerformanceDto findById(Long id);

    List<PerformanceDto> findAll();

    void delete(Long id);
}
