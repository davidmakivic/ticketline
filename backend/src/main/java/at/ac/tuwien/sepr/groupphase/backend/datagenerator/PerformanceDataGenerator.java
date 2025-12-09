package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Date;

@Profile("generateData")
@Component
public class PerformanceDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_PERFORMANCES_TO_GENERATE = 5;

    private final PerformanceRepository performanceRepository;
    private final EventRepository eventRepository;
    private final HallRepository hallRepository;
    private final VenueRepository venueRepository;

    public PerformanceDataGenerator(
        PerformanceRepository performanceRepository,
        EventRepository eventRepository,
        HallRepository hallRepository,
        VenueRepository venueRepository
    ) {
        this.performanceRepository = performanceRepository;
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
        this.venueRepository = venueRepository;
    }

    @PostConstruct
    public void generatePerformanceData() {
        if (!performanceRepository.findAll().isEmpty()) {
            LOGGER.debug("Performances already generated");
            return;
        }

        LOGGER.debug("Generating {} performance entries", NUMBER_OF_PERFORMANCES_TO_GENERATE);

        Venue venue = venueRepository.findAll().stream().findFirst().orElseGet(() -> {
            Venue v = new Venue();
            v.setName("Test Venue");
            LOGGER.debug("Saving venue {}", v);
            return venueRepository.save(v);
        });

        Hall hall = hallRepository.findAll().stream().findFirst().orElseGet(() -> {
            Hall h = new Hall();
            h.setName("Main Hall");
            h.setVenue(venue);
            LOGGER.debug("Saving hall {}", h);
            return hallRepository.save(h);
        });


        Event event = eventRepository.findAll().stream().findFirst().orElseGet(() -> {
            Event e = new Event();
            e.setTitle("Test Event");
            e.setDurationMinutes(90);
            LOGGER.debug("Saving event {}", e);
            return eventRepository.save(e);
        });

        long now = System.currentTimeMillis();

        for (int i = 0; i < NUMBER_OF_PERFORMANCES_TO_GENERATE; i++) {
            Performance p = new Performance();
            p.setEvent(event);
            p.setHall(hall);

            Date start = new Date(now + i * 2 * 60 * 60 * 1000L);
            Date end   = new Date(start.getTime() + 90 * 60 * 1000L);

            p.setStartTime(start);
            p.setEndTime(end);
            p.setBasePriceCents(2500L + i * 500);

            LOGGER.debug("Saving performance {}", p);
            performanceRepository.save(p);
        }
    }
}
