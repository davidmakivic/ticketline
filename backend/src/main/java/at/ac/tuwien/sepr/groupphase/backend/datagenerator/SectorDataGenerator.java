package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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

            Sector s1 = new Sector(hall, "A1", SectorType.SEATED,   catA, "1");
            Sector s2 = new Sector(hall, "A2", SectorType.SEATED,   catA, "2");
            Sector s3 = new Sector(hall, "B1", SectorType.VIP,      catB, "3");
            Sector s4 = new Sector(hall, "B2", SectorType.VIP,      catB, "4");
            Sector s5 = new Sector(hall, "C1", SectorType.STANDING, catC, "5");
            Sector s6 = new Sector(hall, "C2", SectorType.STANDING, catC, "6");
            Sector s7 = new Sector(hall, "D1", SectorType.SEATED, catA, "7");
            Sector s8 = new Sector(hall, "D2", SectorType.SEATED, catA, "8");
            Sector s9 = new Sector(hall, "E1", SectorType.STANDING, catC, "9");
            Sector s10 = new Sector(hall, "E2", SectorType.VIP,      catB, "10");
            Sector s11 = new Sector(hall, "F2", SectorType.SEATED, catA, "11");
            Sector s12 = new Sector(hall, "F1", SectorType.SEATED, catA, "12");
            Sector s13 = new Sector(hall, "G1", SectorType.SEATED,   catA, "13");
            Sector s14 = new Sector(hall, "G2", SectorType.SEATED,   catA, "14");
            Sector s15 = new Sector(hall, "H1", SectorType.VIP,      catB, "15");
            Sector s16 = new Sector(hall, "H2", SectorType.VIP,      catB, "16");
            Sector s17 = new Sector(hall, "I1", SectorType.SEATED, catA, "17");

            sectorRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17));
        }

        LOG.debug("Sector generation complete");
    }
}
