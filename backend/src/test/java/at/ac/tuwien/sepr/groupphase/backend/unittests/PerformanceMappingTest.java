package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PerformanceMappingTest {

    @Autowired
    private PerformanceMapper performanceMapper;

    private Performance buildPerformance() {
        Event event = new Event();
        event.setId(10L);

        Hall hall = new Hall();
        hall.setId(20L);

        Performance performance = new Performance();
        performance.setId(1L);
        performance.setEvent(event);
        performance.setHall(hall);
        performance.setStartTime(new Date(1733596800000L));
        performance.setEndTime(new Date(1733600400000L));
        performance.setBasePriceCents(1500);
        return performance;
    }

    @Test
    void givenPerformance_whenMapToDto_thenAllFieldsMapped() {
        Performance performance = buildPerformance();

        PerformanceDto dto = performanceMapper.performanceToPerformanceDto(performance);

        assertAll(
            () -> assertEquals(performance.getId(), dto.getId()),
            () -> assertEquals(performance.getEvent().getId(), dto.getEventId()),
            () -> assertEquals(performance.getHall().getId(), dto.getHallId()),
            () -> assertEquals(performance.getStartTime(), dto.getStartTime()),
            () -> assertEquals(performance.getEndTime(), dto.getEndTime()),
            () -> assertEquals(performance.getBasePriceCents(), dto.getBasePriceCents())
        );
    }

    @Test
    void givenDto_whenMapToEntity_thenSimpleFieldsMappedAndRelationsIgnored() {
        PerformanceDto dto = new PerformanceDto();
        dto.setId(5L);
        dto.setEventId(10L);
        dto.setHallId(20L);
        dto.setStartTime(new Date());
        dto.setEndTime(new Date());
        dto.setBasePriceCents(2000);

        Performance entity = performanceMapper.performanceDtoToPerformance(dto);

        assertAll(
            () -> assertNull(entity.getId(), "id should be null because it is ignored"),
            () -> assertNull(entity.getEvent(), "event should be null because it is ignored"),
            () -> assertNull(entity.getHall(), "hall should be null because it is ignored"),
            () -> assertEquals(dto.getStartTime(), entity.getStartTime()),
            () -> assertEquals(dto.getEndTime(), entity.getEndTime()),
            () -> assertEquals(dto.getBasePriceCents(), entity.getBasePriceCents())
        );
    }

    @Test
    void givenListOfPerformances_whenMapToDtoList_thenSizeAndContentMatch() {
        Performance p1 = buildPerformance();
        Performance p2 = buildPerformance();

        List<PerformanceDto> dtos =
            performanceMapper.performanceListToPerformanceDtoList(List.of(p1, p2));

        assertEquals(2, dtos.size());
        PerformanceDto first = dtos.get(0);

        assertAll(
            () -> assertEquals(p1.getId(), first.getId()),
            () -> assertEquals(p1.getEvent().getId(), first.getEventId()),
            () -> assertEquals(p1.getHall().getId(), first.getHallId())
        );
    }
}
