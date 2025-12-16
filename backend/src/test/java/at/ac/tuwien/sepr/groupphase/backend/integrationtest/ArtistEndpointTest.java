package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateArtist() throws Exception {
        mockMvc.perform(multipart("/api/v1/artists")
                .file("image", new byte[0])
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("stageName", "JD")
                .param("artistType", "SOLO")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.stageName").value("JD"))
            .andExpect(jsonPath("$.artistType").value("SOLO"));
    }

    @Transactional
    @Test
    void testGetArtistById() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/v1/artists")
                .file("image", new byte[0])
                .param("firstName", "Jane")
                .param("lastName", "Smith")
                .param("stageName", "JS")
                .param("artistType", "BAND")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        ArtistDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(), ArtistDto.class);

        mockMvc.perform(get("/api/v1/artists/" + responseDto.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"))
            .andExpect(jsonPath("$.artistType").value("BAND"));
    }

    @Transactional
    @Test
    void testUpdateArtist() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/v1/artists")
                .file("image", new byte[0])
                .param("firstName", "Bob")
                .param("lastName", "Builder")
                .param("stageName", "BB")
                .param("artistType", "SOLO")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        ArtistDto created = objectMapper.readValue(result.getResponse().getContentAsString(), ArtistDto.class);

        mockMvc.perform(multipart("/api/v1/artists/" + created.getId())
                .file("image", new byte[0])
                .param("firstName", "Robert")
                .param("lastName", "Constructor")
                .param("stageName", "RC")
                .param("artistType", "BAND")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Robert"))
            .andExpect(jsonPath("$.artistType").value("BAND"));
    }

    @Transactional
    @Test
    void testUpdateArtistNotFound() throws Exception {
        mockMvc.perform(multipart("/api/v1/artists/999")
                .file("image", new byte[0])
                .param("firstName", "Test")
                .param("lastName", "Test")
                .param("stageName", "Test")
                .param("artistType", "SOLO")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void testGetAllArtists() throws Exception {
        mockMvc.perform(multipart("/api/v1/artists")
                .file("image", new byte[0])
                .param("firstName", "Artist1")
                .param("lastName", "Last1")
                .param("stageName", "A1")
                .param("artistType", "SOLO")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/v1/artists")
                .file("image", new byte[0])
                .param("firstName", "Artist2")
                .param("lastName", "Last2")
                .param("stageName", "A2")
                .param("artistType", "BAND")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
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
            .andExpect(status().is4xxClientError());
    }

    @Transactional
    @Test
    void testDeleteArtist() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/v1/artists")
                .file("image", new byte[0])
                .param("firstName", "Delete")
                .param("lastName", "Me")
                .param("stageName", "DM")
                .param("artistType", "SOLO")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        ArtistDto created = objectMapper.readValue(result.getResponse().getContentAsString(), ArtistDto.class);

        mockMvc.perform(delete("/api/v1/artists/" + created.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/artists/" + created.getId()))
            .andExpect(status().isNotFound());
    }
}
