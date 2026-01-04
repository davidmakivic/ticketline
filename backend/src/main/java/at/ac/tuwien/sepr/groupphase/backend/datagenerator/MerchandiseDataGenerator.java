package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class MerchandiseDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MerchandiseRepository merchandiseRepository;

    public MerchandiseDataGenerator(MerchandiseRepository merchandiseRepository) {
        this.merchandiseRepository = merchandiseRepository;
    }

    @PostConstruct
    public void generateMerchandiseData() {
        if (!merchandiseRepository.findAll().isEmpty()) {
            LOGGER.debug("Merchandise already generated");
            return;
        }

        LOGGER.debug("Generating merchandise entries");

        Merchandise poster = new Merchandise();
        poster.setName("Band Poster");
        poster.setDescription("High quality poster of the band");
        poster.setPrice(10);
        poster.setQuantity(50);

        merchandiseRepository.save(poster);


        Merchandise tshirt = new Merchandise();
        tshirt.setName("T-Shirt");
        tshirt.setDescription("Official band T-shirt");
        tshirt.setPrice(25);
        tshirt.setQuantity(100);

        merchandiseRepository.save(tshirt);


        Merchandise hoddie = new Merchandise();
        hoddie.setName("Hoodie");
        hoddie.setDescription("Official band hoodie");
        hoddie.setPrice(50);
        hoddie.setQuantity(100);

        merchandiseRepository.save(hoddie);

        Merchandise cup = new Merchandise();
        cup.setName("cup");
        cup.setDescription("Cup with band Logo on it");
        cup.setPrice(10);
        cup.setQuantity(100);
        merchandiseRepository.save(cup);


        Merchandise vinyl = new Merchandise();
        vinyl.setName("Vinyl Album");
        vinyl.setDescription("Limited edition vinyl album");
        vinyl.setPrice(40);
        vinyl.setQuantity(20);
        merchandiseRepository.save(vinyl);


        Merchandise mug = new Merchandise();
        mug.setName("Coffee Mug");
        mug.setDescription("Ceramic mug with band logo");
        mug.setPrice(15);
        mug.setQuantity(75);
        merchandiseRepository.save(mug);


        Merchandise tankTop = new Merchandise();
        tankTop.setName("Tank Top");
        tankTop.setDescription("Tank top with band logo");
        tankTop.setPrice(15);
        tankTop.setQuantity(100);
        merchandiseRepository.save(tankTop);


        LOGGER.debug("Merchandise data generated successfully");
    }


}
