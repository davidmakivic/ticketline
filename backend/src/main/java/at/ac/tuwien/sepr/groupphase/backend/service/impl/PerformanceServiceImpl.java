package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.specification.PerformanceSpecifications;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Date;

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

        performance.setEvent(eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new NotFoundException("Event " + dto.getEventId() + " not found")));
        performance.setHall(hallRepository.findById(dto.getHallId())
            .orElseThrow(() -> new NotFoundException("Hall " + dto.getHallId() + " not found")));

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
        performance.setEvent(eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new NotFoundException("Event " + dto.getEventId() + " not found")));
        performance.setHall(hallRepository.findById(dto.getHallId())
            .orElseThrow(() -> new NotFoundException("Hall " + dto.getHallId() + " not found")));

        Performance saved = performanceRepository.save(performance);
        return performanceMapper.performanceToPerformanceDto(saved);
    }

    @Override
    @Transactional
    public PerformanceDto findById(Long id) {
        LOGGER.info("Fetching performance with id={}", id);
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Performance with id " + id + " not found"));

        return performanceMapper.performanceToPerformanceDto(performance);
    }

    @Override
    public Page<PerformanceDto> findAll(int page, int size) {
        LOGGER.info("Fetching all performances with pagination");
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        return performanceRepository.findAllWithDetails(pageable)
            .map(performanceMapper::performanceToPerformanceDto);
    }

    @Override
    public Page<PerformanceDto> findByAdvancedFilters(
        String title, String artist, String location,
        EventType eventType, Date startDate, Integer durationMinutes,
        int page, int size) {
        LOGGER.info("Searching performances with filters: title={}, artist={}, location={}, eventType={}, startDate={}, duration={}",
            title, artist, location, eventType, startDate, durationMinutes);

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());

        Specification<Performance> spec = Specification.allOf(
            PerformanceSpecifications.hasEventTitle(title),
            PerformanceSpecifications.hasArtist(artist),
            PerformanceSpecifications.hasLocation(location),
            PerformanceSpecifications.hasEventType(eventType),
            PerformanceSpecifications.hasStartDateAfter(startDate),
            PerformanceSpecifications.hasDuration(durationMinutes),
            PerformanceSpecifications.fetchDetails()
        );

        return performanceRepository.findAll(spec, pageable)
            .map(performanceMapper::performanceToPerformanceDto);
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
