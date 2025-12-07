package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class PerformanceServiceTest {

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Event event;
    private Hall hall;

    @BeforeEach
    public void beforeEach() {
        performanceRepository.deleteAll();
        eventRepository.deleteAll();
        hallRepository.deleteAll();
        venueRepository.deleteAll();

        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue = venueRepository.save(venue);

        hall = new Hall();
        hall.setName("Main Hall");
        hall.setVenue(venue);
        hall = hallRepository.save(hall);

        event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test description");
        event.setCategory(EventType.CONCERT);
        event.setDurationMinutes(90);
        event = eventRepository.save(event);
    }

    private PerformanceDto buildDto(Long eventId, Long hallId, int basePriceCents) {
        PerformanceDto dto = new PerformanceDto();
        dto.setEventId(eventId);
        dto.setHallId(hallId);
        dto.setStartTime(new Date());
        dto.setEndTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000));
        dto.setBasePriceCents(basePriceCents);
        return dto;
    }

    @Transactional
    @Test
    void testCreatePerformance() {
        PerformanceDto dto = buildDto(event.getId(), hall.getId(), 2000);

        PerformanceDto saved = performanceService.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEventId()).isEqualTo(event.getId());
        assertThat(saved.getHallId()).isEqualTo(hall.getId());
        assertThat(saved.getBasePriceCents()).isEqualTo(2000);

        assertThat(performanceRepository.findAll()).hasSize(1);
    }

    @Transactional
    @Test
    void testUpdatePerformance() {
        PerformanceDto dto = buildDto(event.getId(), hall.getId(), 2500);
        PerformanceDto created = performanceService.create(dto);
        Long id = created.getId();

        Event newEvent = new Event();
        newEvent.setTitle("Updated Event");
        newEvent.setDescription("Updated desc");
        newEvent.setCategory(EventType.MUSICAL);
        newEvent.setDurationMinutes(120);
        newEvent = eventRepository.save(newEvent);

        Hall newHall = new Hall();
        newHall.setName("Side Hall");
        newHall.setVenue(hall.getVenue());
        newHall = hallRepository.save(newHall);

        PerformanceDto updateDto = new PerformanceDto();
        updateDto.setEventId(newEvent.getId());
        updateDto.setHallId(newHall.getId());
        updateDto.setStartTime(new Date());
        updateDto.setEndTime(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000));
        updateDto.setBasePriceCents(3000);

        PerformanceDto updated = performanceService.update(id, updateDto);

        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getEventId()).isEqualTo(newEvent.getId());
        assertThat(updated.getHallId()).isEqualTo(newHall.getId());
        assertThat(updated.getBasePriceCents()).isEqualTo(3000);
    }

    @Transactional
    @Test
    void testUpdatePerformanceNotFound() {
        PerformanceDto dto = buildDto(event.getId(), hall.getId(), 2000);

        assertThatThrownBy(() -> performanceService.update(999999L, dto))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAllPerformances() {
        PerformanceDto dto1 = buildDto(event.getId(), hall.getId(), 1500);
        PerformanceDto dto2 = buildDto(event.getId(), hall.getId(), 2500);

        performanceService.create(dto1);
        performanceService.create(dto2);

        List<PerformanceDto> result = performanceService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testFindByIdNotFound() {
        assertThatThrownBy(() -> performanceService.findById(999999L))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testDeletePerformance() {
        PerformanceDto dto = buildDto(event.getId(), hall.getId(), 2200);
        PerformanceDto saved = performanceService.create(dto);
        Long id = saved.getId();

        assertThat(performanceService.findById(id)).isNotNull();

        performanceService.delete(id);

        assertThatThrownBy(() -> performanceService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testDeletePerformanceNotFound() {
        assertThatThrownBy(() -> performanceService.delete(999999L))
            .isInstanceOf(NotFoundException.class);
    }
}
