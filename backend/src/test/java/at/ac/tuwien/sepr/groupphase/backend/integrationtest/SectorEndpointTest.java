package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
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

    @Autowired
    private VenueRepository venueRepository;

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

        sectorDto = new SectorDto( "A", SectorType.SEATED, "B", hall.getId());
    }

    @Test
    void testCreateSector() throws Exception {
        String body = objectMapper.writeValueAsString(sectorDto);

        mockMvc.perform(post("/api/sectors")
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

        hallRepository.save(hall1);
        hallRepository.save(hall2);
        hallRepository.save(hall3);
        sectorRepository.save(new Sector(hall1, "A", SectorType.SEATED, "B"));
        sectorRepository.save(new Sector(hall2, "B", SectorType.VIP, "C"));
        sectorRepository.save(new Sector(hall3, "C", SectorType.STANDING, "A"));

        mockMvc.perform(get("/api/sectors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }
}
