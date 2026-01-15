package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
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
public class SeatEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private PriceCategoryRepository priceCategoryRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private SeatCreateDto seatCreateDto;

    private Hall hall;

    private Venue venue;

    private Sector sector;

    @BeforeEach
    void setup() {
        performanceRepository.deleteAll(); // Add this
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
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

        PriceCategory priceCategory = new PriceCategory();
        priceCategory.setName("Standard");
        priceCategory.setPrice(10);
        priceCategory = priceCategoryRepository.save(priceCategory);

        sector = new Sector();
        sector.setType(SectorType.VIP);
        sector.setHall(hall);
        sector.setName("VIP-Sektor");
        sector.setPriceCategory(priceCategory);
        sector.setSectorKey("1");
        sector = sectorRepository.save(sector);

        seatRepository.deleteAll();
        seatCreateDto = new SeatCreateDto(1, 1, sector.getId());
    }

    @Test
    void testCreateSeat() throws Exception {
        String body = objectMapper.writeValueAsString(seatCreateDto);

        mockMvc.perform(post("/api/v1/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.rowNumber").value(1));
    }

    @Test
    void testGetAllSeats() throws Exception {
        seatRepository.save(new Seat(1, 1, sector));
        seatRepository.save(new Seat(2, 2, sector));

        mockMvc.perform(get("/api/v1/seats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
