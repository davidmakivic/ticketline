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
    private final HallRepository hallRepository;
    private final PriceCategoryRepository priceCategoryRepository;

    public SectorDataGenerator(
        SectorRepository sectorRepository,
        HallRepository hallRepository,
        PriceCategoryRepository priceCategoryRepository
    ) {
        this.sectorRepository = sectorRepository;
        this.hallRepository = hallRepository;
        this.priceCategoryRepository = priceCategoryRepository;
    }

    @PostConstruct
    public void generateSectors() {
        if (!sectorRepository.findAll().isEmpty()) {
            LOG.debug("Sectors already generated — skipping");
            return;
        }

        List<Hall> halls = hallRepository.findAll();
        if (halls.isEmpty()) {
            LOG.error("No halls found — cannot generate sectors!");
            return;
        }

        List<PriceCategory> categories = priceCategoryRepository.findAll();
        if (categories.isEmpty()) {
            LOG.error("No price categories found — cannot generate sectors!");
            return;
        }

        // Beispiel: nur für die erste Hall ein kleines Set an Sektoren
        Hall hall = halls.get(0);
        PriceCategory catA = categories.get(0);
        PriceCategory catB = categories.get(Math.min(1, categories.size() - 1));
        PriceCategory catC = categories.get(Math.min(2, categories.size() - 1));

        Sector s1 = new Sector(hall, "A1", SectorType.SEATED, catA, "1");
        Sector s2 = new Sector(hall, "A2", SectorType.SEATED, catA, "2");
        Sector s3 = new Sector(hall, "B1", SectorType.VIP,    catB, "3");
        Sector s4 = new Sector(hall, "C1", SectorType.STANDING, catC, "4");

        sectorRepository.saveAll(List.of(s1, s2, s3, s4));
        LOG.debug("Sector generation complete");
    }
}
