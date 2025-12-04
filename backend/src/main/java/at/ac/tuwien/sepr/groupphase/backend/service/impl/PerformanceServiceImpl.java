package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    private PerformanceRepository performanceRepository;
    private PerformanceMapper performanceMapper;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository, PerformanceMapper performanceMapper) {
        this.performanceRepository = performanceRepository;
        this.performanceMapper = performanceMapper;
    }

    @Override
    public PerformanceDto create(PerformanceDto dto) {
        return null;
    }

    @Override
    public PerformanceDto update(Long id, PerformanceDto dto) {
        return null;
    }

    @Override
    public PerformanceDto findById(Long id) {
        return null;
    }

    @Override
    public List<PerformanceDto> findAll() {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
