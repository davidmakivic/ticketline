package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@Component
public class SeatDataGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SeatRepository seatRepository;
    private final SectorRepository sectorRepository;

    public SeatDataGenerator(SeatRepository seatRepository, SectorRepository sectorRepository) {
        this.seatRepository = seatRepository;
        this.sectorRepository = sectorRepository;
    }

    @PostConstruct
    public void generateSeats() {
        if (!seatRepository.findAll().isEmpty()) {
            LOG.debug("Seats already generated — skipping");
            return;
        }

        LOG.debug("Generating seats for all sectors");

        List<Sector> sectors = sectorRepository.findAll();

        if (sectors.isEmpty()) {
            LOG.error("No sectors found — cannot generate seats!");
            return;
        }

        List<Seat> seatsToSave = new ArrayList<>();


        for (Sector sector : sectors) {
            for (int row = 1; row <= 5; row++) {
                for (int seatNo = 1; seatNo <= 10; seatNo++) {

                    Seat seat = new Seat();
                    seat.setRowNumber(row);
                    seat.setSeatNumber(seatNo);
                    seat.setSector(sector);

                    seatsToSave.add(seat);
                }
            }
        }

        seatRepository.saveAll(seatsToSave);

        LOG.debug("Seat generation complete ({} seats)", seatsToSave.size());
    }


}
