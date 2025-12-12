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

        LOGGER.debug("Generating {} performance entries");

        List<Event> events = eventRepository.findAll();
        List<Hall> halls = hallRepository.findAll();

        if (events.isEmpty() || halls.isEmpty()) {
            LOGGER.warn("No events or halls available – cannot generate performances");
            return;
        }

        long now = System.currentTimeMillis();
        int counter = 0;

        for (Event event : events) {
            for (Hall hall : halls) {
                Performance p = new Performance();
                p.setEvent(event);
                p.setHall(hall);

                Date start = new Date(now + counter * 2L * 60 * 60 * 1000);
                Date end   = new Date(start.getTime() + event.getDurationMinutes() * 60L * 1000);

                p.setStartTime(start);
                p.setEndTime(end);
                p.setBasePriceCents(2500L + counter * 500);

                performanceRepository.save(p);
                counter++;
            }
        }
    }
}
