package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PriceCategoryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;

import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SectorEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private SectorDto sectorDto;

    private Hall hall;

    private Venue venue;

    private PriceCategory priceCategory;
    private PriceCategoryDto priceCategoryDto;

    @Autowired
    private PriceCategoryRepository priceCategoryRepository;

    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private PriceCategoryMapper priceCategoryMapper;

    @BeforeEach
    void setup() {
        sectorRepository.deleteAll();
        hallRepository.deleteAll();
        venueRepository.deleteAll();

        venue = new Venue();
        venue.setName("Venue");
        venueRepository.save(venue);

        hall = new Hall();
        hall.setName("Hall");
        hall.setVenue(venue);
        hallRepository.save(hall);

        priceCategory = new PriceCategory();
        priceCategory.setPrice(20);
        priceCategory.setName("A");
        priceCategoryDto = priceCategoryMapper.priceCategoryToPriceCategoryDto(
            priceCategoryRepository.save(priceCategory));


        sectorDto = new SectorDto( "A", SectorType.SEATED,  hall.getId(), priceCategory.getId());
    }

    @Test
    void testCreateSector() throws Exception {
        String body = objectMapper.writeValueAsString(sectorDto);

        mockMvc.perform(post("/api/v1/sectors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("A"));
    }

    @Test
    void testGetAllSectors() throws Exception {
        Venue venue = new Venue();
        venue.setName("Venue");
        venueRepository.save(venue);

        Hall hall1 = new Hall();
        hall1.setName("Hall1");
        hall1.setVenue(venue);
        hallRepository.save(hall1);

        Hall hall2 = new Hall();
        hall2.setName("Hall2");
        hall2.setVenue(venue);
        hallRepository.save(hall2);

        Hall hall3 = new Hall();
        hall3.setName("Hall3");
        hall3.setVenue(venue);
        hallRepository.save(hall3);


        PriceCategory priceCategory1 = new PriceCategory();
        priceCategory1.setName("Price Category 1");
        priceCategory1.setPrice(10);
        priceCategoryRepository.save(priceCategory1);

        PriceCategory priceCategory2 = new PriceCategory();
        priceCategory2.setName("Price Category 2");
        priceCategory2.setPrice(20);
        priceCategoryRepository.save(priceCategory2);

        PriceCategory priceCategory3 = new PriceCategory();
        priceCategory3.setName("Price Category 3");
        priceCategory3.setPrice(30);
        priceCategoryRepository.save(priceCategory3);


        sectorRepository.save(new Sector(hall1, "A", SectorType.SEATED, priceCategory1));
        sectorRepository.save(new Sector(hall2, "B", SectorType.VIP, priceCategory2));
        sectorRepository.save(new Sector(hall3, "C", SectorType.STANDING, priceCategory3));

        mockMvc.perform(get("/api/v1/sectors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }
}
