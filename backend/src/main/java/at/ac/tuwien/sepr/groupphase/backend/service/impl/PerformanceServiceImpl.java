package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;
    private final EventRepository eventRepository;
    private final HallRepository hallRepository;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository, PerformanceMapper performanceMapper, EventRepository eventRepository, HallRepository hallRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceMapper = performanceMapper;
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public PerformanceDto create(PerformanceDto dto) {
        Performance performance = performanceMapper.performanceDtoToPerformance(dto);

        performance.setEvent(eventRepository.getReferenceById(dto.getEventId()));
        performance.setHall(hallRepository.getReferenceById(dto.getHallId()));

        Performance saved = performanceRepository.save(performance);
        return performanceMapper.performanceToPerformanceDto(saved);
    }


    @Override
    public PerformanceDto update(Long id, PerformanceDto dto) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Performance " + id + " not found"));

        performance.setStartTime(dto.getStartTime());
        performance.setEndTime(dto.getEndTime());
        performance.setBasePriceCents(dto.getBasePriceCents());
        performance.setEvent(eventRepository.getReferenceById(dto.getEventId()));
        performance.setHall(hallRepository.getReferenceById(dto.getHallId()));

        Performance saved = performanceRepository.save(performance);

        return performanceMapper.performanceToPerformanceDto(saved);
    }


    @Override
    public PerformanceDto findById(Long id) {
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Performance with id " + id + " not found"));

        return performanceMapper.performanceToPerformanceDto(performance);
    }

    @Override
    public List<PerformanceDto> findAll() {
        return performanceMapper.performanceListToPerformanceDtoList(
            performanceRepository.findAll()
        );
    }

    @Override
    public void delete(Long id) {
        if (!performanceRepository.existsById(id)) {
            throw new NotFoundException("Performance with id " + id + " not found");
        }

        performanceRepository.deleteById(id);
    }

}
