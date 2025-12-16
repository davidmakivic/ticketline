package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import at.ac.tuwien.sepr.groupphase.backend.util.EventTestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private EventRepository eventRepository;


    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateEvent() throws Exception {
        mockMvc.perform(multipart("/api/v1/events")
                .file("image", new byte[0])
                .param("title", "Test Event")
                .param("description", "Desc")
                .param("category", "CONCERT")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Test Event"))
            .andExpect(jsonPath("$.category").value("CONCERT"));
    }


    @Transactional
    @Test
    void testGetEventById() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/v1/events")
                .file("image", new byte[0])
                .param("title", "Test Event")
                .param("description", "Desc")
                .param("category", "FESTIVAL")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        EventDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(), EventDto.class);

        mockMvc.perform(get("/api/v1/events/" + responseDto.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Event"))
            .andExpect(jsonPath("$.category").value("FESTIVAL"));
    }

    @Transactional
    @Test
    void testUpdateEvent() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/v1/events")
                .file("image", new byte[0])
                .param("title", "Test Event")
                .param("description", "Desc")
                .param("category", "CONCERT")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        EventDto created = objectMapper.readValue(result.getResponse().getContentAsString(), EventDto.class);

        mockMvc.perform(multipart("/api/v1/events/" + created.getId())
                .file("image", new byte[0])
                .param("title", "Updated Title")
                .param("description", "Updated Desc")
                .param("category", "MUSICAL")
                .param("durationMinutes", "60")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.category").value("MUSICAL"));
    }

    @Transactional
    @Test
    void testUpdateEventNotFound() throws Exception {
        mockMvc.perform(multipart("/api/v1/events/999")
                .file("image", new byte[0])
                .param("title", "Updated Title")
                .param("description", "Updated Desc")
                .param("category", "CONCERT")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isNotFound());
    }


    @Transactional
    @Test
    void testGetAllEvents() throws Exception {
        mockMvc.perform(multipart("/api/v1/events")
                .file("image", new byte[0])
                .param("title", "Test Event")
                .param("description", "Desc")
                .param("category", "CONCERT")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/v1/events")
                .file("image", new byte[0])
                .param("title", "Test Event")
                .param("description", "Desc")
                .param("category", "MUSICAL")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].category").exists())
            .andExpect(jsonPath("$[1].category").exists());
    }


    @Transactional
    @Test
    void testGetEventNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/events/999"))
            .andExpect(status().is4xxClientError());
    }

    @Transactional
    @Test
    void testDeleteEvent() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/v1/events")
                .file("image", new byte[0])
                .param("title", "Test Event")
                .param("description", "Desc")
                .param("category", "CONCERT")
                .param("durationMinutes", "30")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        EventDto created = objectMapper.readValue(result.getResponse().getContentAsString(), EventDto.class);

        mockMvc.perform(delete("/api/v1/events/" + created.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/events/" + created.getId()))
            .andExpect(status().isNotFound());
    }
}
