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
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Profile("generateData")
@DependsOn({"hallDataGenerator", "priceCategoryDataGenerator"})
@Component
public class SectorDataGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SectorRepository sectorRepository;
    private final PriceCategoryRepository priceCategoryRepository;
    private final HallRepository hallRepository;

    public SectorDataGenerator(SectorRepository sectorRepository,
                               PriceCategoryRepository priceCategoryRepository,
                               HallRepository hallRepository) {
        this.sectorRepository = sectorRepository;
        this.priceCategoryRepository = priceCategoryRepository;
        this.hallRepository = hallRepository;
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

        List<Hall> halls = hallRepository.findAll();
        if (halls.isEmpty()) {
            LOG.error("No halls found — cannot generate sectors!");
            return;
        }

        for (Hall hall : halls) {
            PriceCategory catA = categories.get(0 % categories.size());
            PriceCategory catB = categories.get(1 % categories.size());
            PriceCategory catC = categories.get(2 % categories.size());

            Sector s1 = new Sector(hall, hall.getName() + " A1", SectorType.SEATED, catA);
            Sector s2 = new Sector(hall, hall.getName() + " A2", SectorType.SEATED, catA);
            Sector s3 = new Sector(hall, hall.getName() + " B1", SectorType.VIP, catB);
            Sector s4 = new Sector(hall, hall.getName() + " B2", SectorType.VIP, catB);
            Sector s5 = new Sector(hall, hall.getName() + " C1", SectorType.STANDING, catC);
            Sector s6 = new Sector(hall, hall.getName() + " C2", SectorType.STANDING, catC);

            sectorRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6));
        }

        LOG.debug("Sector generation complete");
    }
}
