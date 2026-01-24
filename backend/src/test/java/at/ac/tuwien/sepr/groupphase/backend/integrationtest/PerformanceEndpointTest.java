package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import at.ac.tuwien.sepr.groupphase.backend.util.PerformanceTestDataFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PerformanceEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Event event;
    private Hall hall;

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        performanceRepository.deleteAll();
        eventRepository.deleteAll();
        hallRepository.deleteAll();
        venueRepository.deleteAll();

        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue = venueRepository.save(venue);

        hall = new Hall();
        hall.setName("Main Hall");
        hall.setVenue(venue);
        hall = hallRepository.save(hall);

        event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test description");
        event.setCategory(EventType.CONCERT);
        event.setDurationMinutes(90);
        event = eventRepository.save(event);
    }

    @Test
    void testCreatePerformance() throws Exception {
        PerformanceDto dto = PerformanceTestDataFactory.create(event.getId(), hall.getId());

        mockMvc.perform(post("/api/v1/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.eventId").value(event.getId()))
            .andExpect(jsonPath("$.hallId").value(hall.getId()))
            .andExpect(jsonPath("$.basePriceCents").value(dto.getBasePriceCents()));
    }

    @Test
    void testGetAllPerformances() throws Exception {
        Performance p1 = new Performance();
        p1.setEvent(event);
        p1.setHall(hall);
        p1.setBasePriceCents(2000L);
        performanceRepository.save(p1);

        Performance p2 = new Performance();
        p2.setEvent(event);
        p2.setHall(hall);
        p2.setBasePriceCents(3000L);
        performanceRepository.save(p2);

        mockMvc.perform(get("/api/v1/performances")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(10));
    }


    @Test
    void testGetPerformanceById() throws Exception {
        PerformanceDto dto = PerformanceTestDataFactory.create(event.getId(), hall.getId());

        String response = mockMvc.perform(post("/api/v1/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        PerformanceDto created = objectMapper.readValue(response, PerformanceDto.class);

        mockMvc.perform(get("/api/v1/performances/" + created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.getId()))
            .andExpect(jsonPath("$.eventId").value(event.getId()))
            .andExpect(jsonPath("$.hallId").value(hall.getId()));
    }

    @Test
    void testGetPerformanceById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/performances/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePerformance() throws Exception {
        PerformanceDto dto = PerformanceTestDataFactory.create(event.getId(), hall.getId());

        String response = mockMvc.perform(post("/api/v1/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        PerformanceDto created = objectMapper.readValue(response, PerformanceDto.class);

        created.setBasePriceCents(9999L);

        mockMvc.perform(put("/api/v1/performances/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(created))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.getId()))
            .andExpect(jsonPath("$.basePriceCents").value(9999));
    }

    @Test
    void testUpdatePerformance_notFound() throws Exception {
        PerformanceDto dto = PerformanceTestDataFactory.create(event.getId(), hall.getId());
        dto.setBasePriceCents(1234L);

        mockMvc.perform(put("/api/v1/performances/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

}
