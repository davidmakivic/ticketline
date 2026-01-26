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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Profile("generateData")
@DependsOn({"eventDataGenerator", "hallDataGenerator"})
@Component
public class PerformanceDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PerformanceRepository performanceRepository;
    private final EventRepository eventRepository;
    private final HallRepository hallRepository;
    private final List<Performance> performances = new ArrayList<>();

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
            LOGGER.debug("Performances already generated — skipping");
            return;
        }

        List<Hall> halls = hallRepository.findAll();
        long eventCount = eventRepository.count();
        int batchSize = 10;
        int totalPerformances = 0;

        for (int offset = 0; offset < eventCount; offset += batchSize) {
            // Lade nur 10 Events auf einmal
            List<Event> eventBatch = eventRepository.findAll(PageRequest.of(offset / batchSize, batchSize)).getContent();

            for (Event event : eventBatch) {
                List<Performance> performances = generatePerformancesFor(event, halls);
                if (!performances.isEmpty()) {
                    performanceRepository.saveAll(performances);
                    performanceRepository.flush();
                    totalPerformances += performances.size();
                    performances.clear();
                }
            }

            eventBatch.clear();
            System.gc();
            LOGGER.debug("Processed events {} to {}", offset, Math.min(offset + batchSize, eventCount));
        }

        LOGGER.debug("Generated {} performances total", totalPerformances);
    }


    private Performance createPerformance(Event event, Hall hall, int seed) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        // verteile Performances auf die nächsten30 Tage
        int dayOffset = rnd.nextInt(0, 30);
        // wähle eine Startstunde zwischen10:00 und21:00
        int startHour = rnd.nextInt(10, 21);
        int startMinute = rnd.nextInt(0, 2) * 30; //00 oder30
        LocalDateTime start = LocalDateTime.now()
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
            .plusDays(dayOffset)
            .withHour(startHour)
            .withMinute(startMinute);

        LocalDateTime end = start.plusMinutes(
            event.getDurationMinutes() != null ? event.getDurationMinutes() : 90);

        Performance p = new Performance();
        p.setEvent(event);
        p.setHall(hall);
        p.setStartTime(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
        p.setEndTime(Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
        p.setBasePriceCents(2_500L + seed * 100L + rnd.nextLong(0, 1_000));
        return p;
    }

    private List<Performance> generatePerformancesFor(Event event, List<Hall> halls) {
        List<Performance> generated = new ArrayList<>();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int count = rnd.nextInt(1, 4);

        for (int i = 0; i < count; i++) {
            Hall hall = halls.get(rnd.nextInt(halls.size()));
            generated.add(createPerformance(event, hall, i));
        }

        return generated;
    }

}


