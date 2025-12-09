package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import at.ac.tuwien.sepr.groupphase.backend.util.ArtistTestDataFactory;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ArtistEndpointTest {

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistRepository artistRepository;

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateArtist() throws Exception {
        ArtistDto dto = ArtistTestDataFactory.create(ArtistType.SOLO, "Test");

        mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON).content(toJson(dto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.stageName").value("Test"))
            .andExpect(jsonPath("$.artistType").value("SOLO"));

    }


    @Transactional
    @Test
    void testGetArtistById() throws Exception {
        ArtistDto dto = ArtistTestDataFactory.create(ArtistType.BAND, "The Band");

        String response = mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        ArtistDto created = objectMapper.readValue(response, ArtistDto.class);

        mockMvc.perform(get("/api/v1/artists/" + created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stageName").value("The Band"))
            .andExpect(jsonPath("$.artistType").value("BAND"));
    }


    @Transactional
    @Test
    void testUpdateArtist() throws Exception {
        ArtistDto dto = ArtistTestDataFactory.create(ArtistType.SOLO, "Old Name");

        String response = mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        ArtistDto created = objectMapper.readValue(response, ArtistDto.class);

        created.setStageName("Updated Name");
        created.setArtistType(ArtistType.BAND);

        mockMvc.perform(put("/api/v1/artists/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(created))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stageName").value("Updated Name"))
            .andExpect(jsonPath("$.artistType").value("BAND"));
    }

    @Transactional
    @Test
    void testUpdateArtistNotFound() throws Exception {
        ArtistDto dto = ArtistTestDataFactory.create(ArtistType.SOLO, "Does Not Exist");

        mockMvc.perform(put("/api/v1/artists/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void testGetAllArtists() throws Exception {
        ArtistDto a1 = ArtistTestDataFactory.create(ArtistType.SOLO, "One");
        ArtistDto a2 = ArtistTestDataFactory.create(ArtistType.BAND, "Two");

        mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(a1))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(a2))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/artists"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].artistType").exists())
            .andExpect(jsonPath("$[1].artistType").exists());
    }

    @Transactional
    @Test
    void testGetArtistNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/artists/999"))
            .andExpect(status().isNotFound());
    }


    @Transactional
    @Test
    void testDeleteArtist() throws Exception {
        ArtistDto dto = ArtistTestDataFactory.create(ArtistType.SOLO, "DeleteMe");

        String response = mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dto))
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        ArtistDto created = objectMapper.readValue(response, ArtistDto.class);

        mockMvc.perform(delete("/api/v1/artists/" + created.getId())
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/artists/" + created.getId()))
            .andExpect(status().isNotFound());
    }

}
