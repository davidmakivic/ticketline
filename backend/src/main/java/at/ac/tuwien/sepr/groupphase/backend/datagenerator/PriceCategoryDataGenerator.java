package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class PriceCategoryDataGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PriceCategoryRepository repository;

    public PriceCategoryDataGenerator(PriceCategoryRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void generatePriceCategories() {
        if (!repository.findAll().isEmpty()) {
            LOG.debug("Price categories already exist — skipping generation");
            return;
        }

        LOG.debug("Generating default price categories");

        PriceCategory cheap = new PriceCategory();
        cheap.setName("A");
        cheap.setPrice(10.90);

        PriceCategory medium = new PriceCategory();
        medium.setName("B");
        medium.setPrice(15.50);

        PriceCategory premium = new PriceCategory();
        premium.setName("C");
        premium.setPrice(21.00);

        repository.save(cheap);
        repository.save(medium);
        repository.save(premium);

        LOG.debug("PriceCategory generation complete");
    }
}
