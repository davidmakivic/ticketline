package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import at.ac.tuwien.sepr.groupphase.backend.util.EventTestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class EventEndpointTest {

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @Transactional
    @Test
    void testCreateEvent() throws Exception {
        EventDto dto = EventTestDataFactory.create(EventType.CONCERT);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Test Event"))
            .andExpect(jsonPath("$.category").value("CONCERT"));
    }


    @Transactional
    @Test
    void testGetEventById() throws Exception {
        EventDto dto = EventTestDataFactory.create(EventType.FESTIVAL);

        String response = mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        EventDto responseDto = objectMapper.readValue(response, EventDto.class);

        mockMvc.perform(get("/api/events/" + responseDto.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Event"))
            .andExpect(jsonPath("$.category").value("FESTIVAL"));
    }


    @Transactional
    @Test
    void testGetAllEvents() throws Exception {
        EventDto e1 = EventTestDataFactory.create(EventType.CONCERT);
        EventDto e2 = EventTestDataFactory.create(EventType.MUSICAL);

        String response1 = mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(e1))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(e2))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].category").exists())
            .andExpect(jsonPath("$[1].category").exists());
    }


    @Transactional
    @Test
    void testGetEventNotFound() throws Exception {
        mockMvc.perform(get("/api/events/999"))
            .andExpect(status().is4xxClientError());
    }
}
