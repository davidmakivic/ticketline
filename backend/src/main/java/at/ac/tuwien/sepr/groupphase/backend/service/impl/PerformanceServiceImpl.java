package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
        LOGGER.info("Creating performance");
        LOGGER.debug("Payload: {}", dto);
        Performance performance = performanceMapper.performanceDtoToPerformance(dto);

        performance.setEvent(eventRepository.getReferenceById(dto.getEventId()));
        performance.setHall(hallRepository.getReferenceById(dto.getHallId()));

        Performance saved = performanceRepository.save(performance);
        return performanceMapper.performanceToPerformanceDto(saved);
    }


    @Override
    public PerformanceDto update(Long id, PerformanceDto dto) {
        LOGGER.info("Updating performance with id={}", id);
        LOGGER.debug("Payload: {}", dto);
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
        LOGGER.info("Fetching performance with id={}", id);
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Performance with id " + id + " not found"));

        return performanceMapper.performanceToPerformanceDto(performance);
    }

    @Override
    public List<PerformanceDto> findAll() {
        LOGGER.info("Fetching all performances");
        return performanceMapper.performanceListToPerformanceDtoList(
            performanceRepository.findAll()
        );
    }

    @Override
    public void delete(Long id) {
        LOGGER.info("Deleting performance with id={}", id);
        if (!performanceRepository.existsById(id)) {
            throw new NotFoundException("Performance with id " + id + " not found");
        }

        performanceRepository.deleteById(id);
    }

}
