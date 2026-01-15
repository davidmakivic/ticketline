package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.util.VenueTestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class VenueEndpointTest {

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        performanceRepository.deleteAll();
        venueRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateVenue() throws Exception {
        VenueDto dto = VenueTestDataFactory.create();

        mockMvc.perform(post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Test Name"))
            .andExpect(jsonPath("$.street").value("Test Street"))
            .andExpect(jsonPath("$.city").value("Test City"))
            .andExpect(jsonPath("$.postalCode").value("Test Postal Code"))
            .andExpect(jsonPath("$.country").value("Test Country"));
    }

    @Transactional
    @Test
    void testGetVenueById() throws Exception {
        VenueDto dto = VenueTestDataFactory.create();

        String response = mockMvc.perform(post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        VenueDto responseDto = objectMapper.readValue(response, VenueDto.class);

        mockMvc.perform(get("/api/v1/venues/" + responseDto.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Test Name"))
            .andExpect(jsonPath("$.street").value("Test Street"))
            .andExpect(jsonPath("$.city").value("Test City"))
            .andExpect(jsonPath("$.postalCode").value("Test Postal Code"))
            .andExpect(jsonPath("$.country").value("Test Country"));
    }

    @Transactional
    @Test
    void testUpdateVenue() throws Exception {
        VenueDto dto = VenueTestDataFactory.create();

        String response = mockMvc.perform(post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        VenueDto created = objectMapper.readValue(response, VenueDto.class);

        created.setName("Updated Name");
        created.setStreet("Updated Street");
        created.setCity("Updated City");
        created.setPostalCode("Updated Postal Code");
        created.setCountry("Updated Country");

        mockMvc.perform(put("/api/v1/venues/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(created))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.street").value("Updated Street"))
            .andExpect(jsonPath("$.city").value("Updated City"))
            .andExpect(jsonPath("$.postalCode").value("Updated Postal Code"))
            .andExpect(jsonPath("$.country").value("Updated Country"));
    }

    @Transactional
    @Test
    void testUpdateVenueNotFound() throws Exception {
        VenueDto dto = VenueTestDataFactory.create();
        dto.setName("Updated Name");

        mockMvc.perform(put("/api/v1/venues/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void testGetAllVenues() throws Exception {
        VenueDto dto1 = VenueTestDataFactory.create();
        VenueDto dto2 = VenueTestDataFactory.create();

        mockMvc.perform(post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto1))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        mockMvc.perform(post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto2))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/v1/venues"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").exists())
            .andExpect(jsonPath("$[1].name").exists());
    }

    @Transactional
    @Test
    void testGetVenueNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/venues/999"))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void testDeleteVenue() throws Exception {
        VenueDto dto = VenueTestDataFactory.create();

        String response = mockMvc.perform(post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        VenueDto created  = objectMapper.readValue(response, VenueDto.class);

        mockMvc.perform(delete("/api/v1/venues/" + created.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/venues/" +  created.getId()))
            .andExpect(status().isNotFound());

    }
}
