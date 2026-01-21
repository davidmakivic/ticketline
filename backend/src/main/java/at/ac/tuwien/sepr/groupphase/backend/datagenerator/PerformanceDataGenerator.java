package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Profile("generateData")
@DependsOn({"eventDataGenerator", "hallDataGenerator"})
@Component
public class PerformanceDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PerformanceRepository performanceRepository;
    private final EventRepository eventRepository;
    private final HallRepository hallRepository;

    public PerformanceDataGenerator(
        PerformanceRepository performanceRepository,
        EventRepository eventRepository,
        HallRepository hallRepository
    ) {
        this.performanceRepository = performanceRepository;
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
    }

    @PostConstruct
    public void generatePerformanceData() {
        if (!performanceRepository.findAll().isEmpty()) {
            LOGGER.debug("Performances already generated");
            return;
        }

        List<Event> events = eventRepository.findAll();
        List<Hall> halls = hallRepository.findAll();

        if (events.isEmpty() || halls.isEmpty()) {
            LOGGER.warn("No events or halls available – cannot generate performances");
            return;
        }

        LOGGER.debug("Generating {} performance entries", (long) events.size() * halls.size());

        var zone = java.time.ZoneId.systemDefault();
        var baseDate = java.time.LocalDate.now(zone);

        int eventIndex = 0;
        List<Performance> batch = new ArrayList<>(50);

        for (Event event : events) {
            int hallIndex = 0;
            for (Hall hall : halls) {
                var eventDate = baseDate.plusDays(hallIndex * 2L);
                int startHour = Math.min(16 + hallIndex, 21);

                var startLdt = eventDate.atTime(startHour, 0);
                var endLdt = startLdt.plusMinutes(event.getDurationMinutes());

                Date start = Date.from(startLdt.atZone(zone).toInstant());
                Date end = Date.from(endLdt.atZone(zone).toInstant());

                Performance p = new Performance();
                p.setEvent(event);
                p.setHall(hall);
                p.setStartTime(start);
                p.setEndTime(end);
                p.setBasePriceCents(2500L + (eventIndex * 500L) + (hallIndex * 100L));

                batch.add(p);

                // Batch speichern
                if (batch.size() >= 50) {
                    performanceRepository.saveAll(batch);
                    performanceRepository.flush();
                    batch.clear();
                }

                hallIndex++;
            }
            eventIndex++;
        }

        // Rest speichern
        if (!batch.isEmpty()) {
            performanceRepository.saveAll(batch);
            performanceRepository.flush();
        }
    }



}


