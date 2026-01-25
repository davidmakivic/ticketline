package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.MerchandiseVariant;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.MerchandiseSize;
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
        tshirt.getVariants().add(createVariant(tshirt, MerchandiseSize.S, 25));
        tshirt.getVariants().add(createVariant(tshirt, MerchandiseSize.M, 30));
        tshirt.getVariants().add(createVariant(tshirt, MerchandiseSize.L, 25));
        tshirt.getVariants().add(createVariant(tshirt, MerchandiseSize.XL, 20));
        loadImageFromFile(tshirt, new ClassPathResource("images/bandTshirt.png"));
        merchandiseRepository.save(tshirt);

        // Hoodie (Größen S, M, L, XL)
        Merchandise hoodie = new Merchandise();
        hoodie.setName("Hoodie");
        hoodie.setDescription("Official band hoodie");
        hoodie.setPrice(5000);
        hoodie.getVariants().add(createVariant(hoodie, MerchandiseSize.S, 20));
        hoodie.getVariants().add(createVariant(hoodie, MerchandiseSize.M, 30));
        hoodie.getVariants().add(createVariant(hoodie, MerchandiseSize.L, 30));
        hoodie.getVariants().add(createVariant(hoodie, MerchandiseSize.XL, 20));
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
        tankTop.getVariants().add(createVariant(tankTop, MerchandiseSize.S, 20));
        tankTop.getVariants().add(createVariant(tankTop, MerchandiseSize.M, 30));
        tankTop.getVariants().add(createVariant(tankTop, MerchandiseSize.L, 30));
        tankTop.getVariants().add(createVariant(tankTop, MerchandiseSize.XL, 20));
        loadImageFromFile(tankTop, new ClassPathResource("images/bandTankTop.png"));
        merchandiseRepository.save(tankTop);

        // Beanie
        Merchandise beanie = new Merchandise();
        beanie.setName("Beanie");
        beanie.setDescription("Warm beanie with stitched band logo");
        beanie.setPrice(1800);
        beanie.getVariants().add(createVariant(beanie, null, 60));
        loadImageFromFile(beanie, new ClassPathResource("images/beanie.png"));
        merchandiseRepository.save(beanie);

        // Cap
        Merchandise cap = new Merchandise();
        cap.setName("Baseball Cap");
        cap.setDescription("Adjustable cap with embroidered band logo");
        cap.setPrice(2200);
        cap.getVariants().add(createVariant(cap, null, 50));
        loadImageFromFile(cap, new ClassPathResource("images/bandCap.png"));
        merchandiseRepository.save(cap);

        // Longsleeve
        Merchandise longsleeve = new Merchandise();
        longsleeve.setName("Longsleeve Shirt");
        longsleeve.setDescription("Longsleeve shirt with band artwork");
        longsleeve.setPrice(3000);
        longsleeve.getVariants().add(createVariant(longsleeve, MerchandiseSize.S, 20));
        longsleeve.getVariants().add(createVariant(longsleeve, MerchandiseSize.M, 30));
        longsleeve.getVariants().add(createVariant(longsleeve, MerchandiseSize.L, 25));
        longsleeve.getVariants().add(createVariant(longsleeve, MerchandiseSize.XL, 15));
        loadImageFromFile(longsleeve, new ClassPathResource("images/longsleeve.png"));
        merchandiseRepository.save(longsleeve);

        // Zip Hoodie
        Merchandise zipHoodie = new Merchandise();
        zipHoodie.setName("Zip Hoodie");
        zipHoodie.setDescription("Zip hoodie with subtle band branding");
        zipHoodie.setPrice(5500);
        zipHoodie.getVariants().add(createVariant(zipHoodie, MerchandiseSize.S, 15));
        zipHoodie.getVariants().add(createVariant(zipHoodie, MerchandiseSize.M, 25));
        zipHoodie.getVariants().add(createVariant(zipHoodie, MerchandiseSize.L, 25));
        zipHoodie.getVariants().add(createVariant(zipHoodie, MerchandiseSize.XL, 15));
        loadImageFromFile(zipHoodie, new ClassPathResource("images/hoodie.png"));
        merchandiseRepository.save(zipHoodie);

        // Tote Bag
        Merchandise toteBag = new Merchandise();
        toteBag.setName("Tote Bag");
        toteBag.setDescription("Canvas tote bag with band print");
        toteBag.setPrice(1200);
        toteBag.getVariants().add(createVariant(toteBag, null, 80));
        loadImageFromFile(toteBag, new ClassPathResource("images/toteBag.png"));
        merchandiseRepository.save(toteBag);

        // Keychain
        Merchandise keychain = new Merchandise();
        keychain.setName("Keychain");
        keychain.setDescription("Metal keychain with band logo");
        keychain.setPrice(700);
        keychain.getVariants().add(createVariant(keychain, null, 150));
        loadImageFromFile(keychain, new ClassPathResource("images/KeyChain.png"));
        merchandiseRepository.save(keychain);

        // Sticker Pack
        Merchandise stickers = new Merchandise();
        stickers.setName("Sticker Pack");
        stickers.setDescription("Set of high quality vinyl stickers");
        stickers.setPrice(500);
        stickers.getVariants().add(createVariant(stickers, null, 200));
        loadImageFromFile(stickers, new ClassPathResource("images/stickerPack.png"));
        merchandiseRepository.save(stickers);

        // Patch
        Merchandise patch = new Merchandise();
        patch.setName("Embroidered Patch");
        patch.setDescription("Iron-on patch with band emblem");
        patch.setPrice(600);
        patch.getVariants().add(createVariant(patch, null, 120));
        loadImageFromFile(patch, new ClassPathResource("images/patch.png"));
        merchandiseRepository.save(patch);

        // Wristband
        Merchandise wristband = new Merchandise();
        wristband.setName("Wristband");
        wristband.setDescription("Fabric wristband with band name");
        wristband.setPrice(400);
        wristband.getVariants().add(createVariant(wristband, null, 200));
        loadImageFromFile(wristband, new ClassPathResource("images/wristband.png"));
        merchandiseRepository.save(wristband);

        // Scarf
        Merchandise scarf = new Merchandise();
        scarf.setName("Scarf");
        scarf.setDescription("Winter scarf with woven band logo");
        scarf.setPrice(2800);
        scarf.getVariants().add(createVariant(scarf, null, 40));
        loadImageFromFile(scarf, new ClassPathResource("images/scarf.png"));
        merchandiseRepository.save(scarf);

        // Socks
        Merchandise socks = new Merchandise();
        socks.setName("Socks");
        socks.setDescription("Comfort socks with band pattern");
        socks.setPrice(1200);
        socks.getVariants().add(createVariant(socks, MerchandiseSize.S, 40));
        socks.getVariants().add(createVariant(socks, MerchandiseSize.M, 60));
        socks.getVariants().add(createVariant(socks, MerchandiseSize.L, 40));
        loadImageFromFile(socks, new ClassPathResource("images/socks.png"));
        merchandiseRepository.save(socks);

        // Phone Case
        Merchandise phoneCase = new Merchandise();
        phoneCase.setName("Phone Case");
        phoneCase.setDescription("Protective phone case with band artwork");
        phoneCase.setPrice(2000);
        phoneCase.getVariants().add(createVariant(phoneCase, null, 70));
        loadImageFromFile(phoneCase, new ClassPathResource("images/phoneCase.png"));
        merchandiseRepository.save(phoneCase);

        // Notebook
        Merchandise notebook = new Merchandise();
        notebook.setName("Notebook");
        notebook.setDescription("A5 notebook with band cover design");
        notebook.setPrice(900);
        notebook.getVariants().add(createVariant(notebook, null, 100));
        loadImageFromFile(notebook, new ClassPathResource("images/notebook.png"));
        merchandiseRepository.save(notebook);

        // Poster Set
        Merchandise posterSet = new Merchandise();
        posterSet.setName("Poster Set");
        posterSet.setDescription("Set of 3 exclusive band posters");
        posterSet.setPrice(2500);
        posterSet.getVariants().add(createVariant(posterSet, null, 40));
        loadImageFromFile(posterSet, new ClassPathResource("images/posterSet.png"));
        merchandiseRepository.save(posterSet);

        // Flag
        Merchandise flag = new Merchandise();
        flag.setName("Band Flag");
        flag.setDescription("Large fabric flag with band logo");
        flag.setPrice(3000);
        flag.getVariants().add(createVariant(flag, null, 30));
        loadImageFromFile(flag, new ClassPathResource("images/flag.png"));
        merchandiseRepository.save(flag);

        // Lanyard
        Merchandise lanyard = new Merchandise();
        lanyard.setName("Lanyard");
        lanyard.setDescription("Lanyard with band branding");
        lanyard.setPrice(600);
        lanyard.getVariants().add(createVariant(lanyard, null, 120));
        loadImageFromFile(lanyard, new ClassPathResource("images/lanyard.png"));
        merchandiseRepository.save(lanyard);

        // CD Album
        Merchandise cd = new Merchandise();
        cd.setName("CD Album");
        cd.setDescription("Standard CD album");
        cd.setPrice(1500);
        cd.getVariants().add(createVariant(cd, null, 60));
        loadImageFromFile(cd, new ClassPathResource("images/cdAlbum.png"));
        merchandiseRepository.save(cd);

        LOGGER.debug("Merchandise data generated successfully");

    }

    private MerchandiseVariant createVariant(Merchandise merchandise, MerchandiseSize size, int quantity) {
        LOGGER.debug("Generating Variant {} for {}", size, merchandise);
        MerchandiseVariant variant = new MerchandiseVariant();
        variant.setMerchandise(merchandise);
        variant.setSize(size);
        variant.setQuantity(quantity);
        LOGGER.debug("Variant data generated successfully");
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
