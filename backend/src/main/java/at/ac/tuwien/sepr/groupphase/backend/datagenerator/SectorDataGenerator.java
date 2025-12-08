package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PriceCategoryService;
import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;

import org.antlr.v4.runtime.misc.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Profile("generateData")
@Component
public class SectorDataGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SectorRepository sectorRepository;
    private final PriceCategoryRepository priceCategoryRepository;
    private HallRepository hallRepository;
    private VenueRepository venueRepository;

    public SectorDataGenerator(SectorRepository sectorRepository, PriceCategoryRepository priceCategoryRepository) {
        this.sectorRepository = sectorRepository;
        this.priceCategoryRepository = priceCategoryRepository;
    }

    @PostConstruct
    public void generateSectors() {
        if (!sectorRepository.findAll().isEmpty()) {
            LOG.debug("Sectors already generated — skipping");
            return;
        }

        LOG.debug("Generating sectors");

        List<PriceCategory> categories = priceCategoryRepository.findAll();

        if (categories.isEmpty()) {
            LOG.error("No price categories found — cannot generate sectors!");
            return;
        }

        Venue venue = new Venue();
        venue.setName("Venue");
        venueRepository.save(venue);

        Hall hall = new Hall();
        hall.setName("Halle 1");
        hall.setVenue(venue);
        hallRepository.save(hall);

        PriceCategory catA = categories.get(0);

        Sector s1 = new Sector(hall, "A1", SectorType.SEATED, catA);
        s1.setPriceCategory(catA);

        Sector s2 = new Sector(hall, "A2", SectorType.SEATED, catA);
        s2.setPriceCategory(catA);


        PriceCategory catB = categories.get(1 % categories.size());

        Sector s3 = new Sector(hall, "B1", SectorType.VIP, catB);
        s3.setPriceCategory(catB);

        Sector s4 = new Sector(hall, "B2", SectorType.VIP, catB);
        s4.setPriceCategory(catB);


        PriceCategory catC = categories.get(2 % categories.size());

        Sector s5 = new Sector(hall, "C1", SectorType.STANDING, catC);
        s5.setPriceCategory(catC);

        Sector s6 = new Sector(hall, "C2", SectorType.STANDING, catC);
        s6.setPriceCategory(catC);

        sectorRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6));

        LOG.debug("Sector generation complete");
    }
}
