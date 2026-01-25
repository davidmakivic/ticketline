package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.MerchandiseVariant;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
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

        // Poster (kein Größenartikel)
        Merchandise poster = new Merchandise();
        poster.setName("Band Poster");
        poster.setDescription("High quality poster of the band");
        poster.setPrice(1000);
        poster.getVariants().add(createVariant(poster, null, 50));
        loadImageFromFile(poster, new ClassPathResource("images/bandPoster.png"));
        merchandiseRepository.save(poster);

        // T-Shirt (Größen S, M, L, XL)
        Merchandise tshirt = new Merchandise();
        tshirt.setName("T-Shirt");
        tshirt.setDescription("Official band T-shirt");
        tshirt.setPrice(2500);
        tshirt.getVariants().add(createVariant(tshirt, "S", 25));
        tshirt.getVariants().add(createVariant(tshirt, "M", 30));
        tshirt.getVariants().add(createVariant(tshirt, "L", 25));
        tshirt.getVariants().add(createVariant(tshirt, "XL", 20));
        loadImageFromFile(tshirt, new ClassPathResource("images/bandTshirt.png"));
        merchandiseRepository.save(tshirt);

        // Hoodie (Größen S, M, L, XL)
        Merchandise hoodie = new Merchandise();
        hoodie.setName("Hoodie");
        hoodie.setDescription("Official band hoodie");
        hoodie.setPrice(5000);
        hoodie.getVariants().add(createVariant(hoodie, "S", 20));
        hoodie.getVariants().add(createVariant(hoodie, "M", 30));
        hoodie.getVariants().add(createVariant(hoodie, "L", 30));
        hoodie.getVariants().add(createVariant(hoodie, "XL", 20));
        loadImageFromFile(hoodie, new ClassPathResource("images/bandHoodie.png"));
        merchandiseRepository.save(hoodie);

        // Cup (kein Größenartikel)
        Merchandise cup = new Merchandise();
        cup.setName("Cup");
        cup.setDescription("Cup with band Logo on it");
        cup.setPrice(1000);
        cup.getVariants().add(createVariant(cup, null, 100));
        loadImageFromFile(cup, new ClassPathResource("images/bandCup.png"));
        merchandiseRepository.save(cup);

        // Vinyl (kein Größenartikel)
        Merchandise vinyl = new Merchandise();
        vinyl.setName("Vinyl Album");
        vinyl.setDescription("Limited edition vinyl album");
        vinyl.setPrice(4000);
        vinyl.getVariants().add(createVariant(vinyl, null, 20));
        loadImageFromFile(vinyl, new ClassPathResource("images/bandVinyl.png"));
        merchandiseRepository.save(vinyl);

        // Coffee Mug (kein Größenartikel)
        Merchandise mug = new Merchandise();
        mug.setName("Coffee Mug");
        mug.setDescription("Ceramic mug with band logo");
        mug.setPrice(1500);
        mug.getVariants().add(createVariant(mug, null, 75));
        loadImageFromFile(mug, new ClassPathResource("images/coffeeMug.png"));
        merchandiseRepository.save(mug);

        // Tank Top (Größen S, M, L, XL)
        Merchandise tankTop = new Merchandise();
        tankTop.setName("Tank Top");
        tankTop.setDescription("Tank top with band logo");
        tankTop.setPrice(1500);
        tankTop.getVariants().add(createVariant(tankTop, "S", 20));
        tankTop.getVariants().add(createVariant(tankTop, "M", 30));
        tankTop.getVariants().add(createVariant(tankTop, "L", 30));
        tankTop.getVariants().add(createVariant(tankTop, "XL", 20));
        loadImageFromFile(tankTop, new ClassPathResource("images/bandTankTop.png"));
        merchandiseRepository.save(tankTop);

        LOGGER.debug("Merchandise data generated successfully");
    }

    private MerchandiseVariant createVariant(Merchandise merchandise, String size, int quantity) {
        MerchandiseVariant variant = new MerchandiseVariant();
        variant.setMerchandise(merchandise);
        variant.setSize(size);
        variant.setQuantity(quantity);
        return variant;
    }

    private void loadImageFromFile(Merchandise merchandise, ClassPathResource img) {
        try (InputStream in = img.getInputStream()) {

            byte[] bytes = in.readAllBytes();
            merchandise.setImageData(bytes);
            merchandise.setImageContentType("image/jpeg");
        } catch (IOException e) {
            LOGGER.warn("Could not load image from {}: {}", img, e.getMessage());
        }
    }
}
