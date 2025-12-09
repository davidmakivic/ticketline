package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import at.ac.tuwien.sepr.groupphase.backend.util.TicketTestDataFactory;
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
public class TicketEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

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

    private Performance performance;

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAll();
        performanceRepository.deleteAll();
        eventRepository.deleteAll();
        hallRepository.deleteAll();
        venueRepository.deleteAll();

        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue = venueRepository.save(venue);

        Hall hall = new Hall();
        hall.setName("Main Hall");
        hall.setVenue(venue);
        hall = hallRepository.save(hall);

        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test description");
        event.setCategory(EventType.CONCERT);
        event.setDurationMinutes(90);
        event = eventRepository.save(event);

        performance = new Performance();
        performance.setEvent(event);
        performance.setHall(hall);
        performance.setBasePriceCents(2000L);
        performance = performanceRepository.save(performance);
    }

    @Test
    void testCreateTicket() throws Exception {
        TicketDto dto = TicketTestDataFactory.create(performance.getId());


        mockMvc.perform(post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.performanceId").value(performance.getId()))
            .andExpect(jsonPath("$.priceFinalCents").value(dto.getPriceFinalCents().intValue()));
    }

    @Test
    void testGetAllTickets() throws Exception {
        Ticket t1 = new Ticket();
        t1.setPerformance(performance);
        t1.setPriceFinalCents(2000L);
        t1.setStatus(TicketStatus.AVAILABLE);
        ticketRepository.save(t1);

        Ticket t2 = new Ticket();
        t2.setPerformance(performance);
        t2.setPriceFinalCents(3000L);
        t2.setStatus(TicketStatus.AVAILABLE);
        ticketRepository.save(t2);

        mockMvc.perform(get("/api/v1/tickets"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void testGetTicketById() throws Exception {
        TicketDto dto = TicketTestDataFactory.create(performance.getId());

        String response = mockMvc.perform(post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        TicketDto created = objectMapper.readValue(response, TicketDto.class);

        mockMvc.perform(get("/api/v1/tickets/" + created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.getId()))
            .andExpect(jsonPath("$.performanceId").value(performance.getId()))
            .andExpect(jsonPath("$.priceFinalCents").value(dto.getPriceFinalCents().intValue()));
    }

    @Test
    void testGetTicketById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTicket() throws Exception {
        TicketDto dto = TicketTestDataFactory.create(performance.getId());

        String response = mockMvc.perform(post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        TicketDto created = objectMapper.readValue(response, TicketDto.class);

        created.setPriceFinalCents(9999L);

        mockMvc.perform(put("/api/v1/tickets/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(created))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.getId()))
            .andExpect(jsonPath("$.priceFinalCents").value(9999));
    }

    @Test
    void testUpdateTicket_notFound() throws Exception {
        TicketDto dto = TicketTestDataFactory.create(performance.getId());


        mockMvc.perform(put("/api/v1/tickets/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTicket() throws Exception {
        TicketDto dto = TicketTestDataFactory.create(performance.getId());

        String response = mockMvc.perform(post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        TicketDto created = objectMapper.readValue(response, TicketDto.class);

        mockMvc.perform(delete("/api/v1/tickets/" + created.getId())
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tickets/" + created.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTicket_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/tickets/999999")
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }
}
