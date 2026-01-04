package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MerchandiseEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private String adminToken;

    @BeforeEach
    void setup() {
        // Alle Merchandise-Einträge löschen
        merchandiseRepository.deleteAll();

        // Token für Admin erzeugen
        adminToken = jwtTokenizer.getAuthToken(
            ADMIN_USER, ADMIN_ROLES);
    }

    @Test
    void givenNoMerchandise_whenGetAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/merchandise")
                .header(securityProperties.getAuthHeader(), adminToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        MerchandiseDto[] dtos = objectMapper.readValue(response.getContentAsString(), MerchandiseDto[].class);
        assertEquals(0, dtos.length);
    }

    @Test
    void givenOneMerchandise_whenGetAll_thenListWithOneItem() throws Exception {
        Merchandise merchandise = new Merchandise();
        merchandise.setName("T-Shirt");
        merchandise.setDescription("Band T-Shirt");
        merchandise.setPrice(25);
        merchandise.setQuantity(100);
        merchandiseRepository.save(merchandise);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/merchandise")
                .header(securityProperties.getAuthHeader(), adminToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        MerchandiseDto[] dtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MerchandiseDto[].class);
        assertEquals(1, dtos.length);
        assertEquals("T-Shirt", dtos[0].getName());
        assertEquals(25, dtos[0].getPrice());
    }

    @Test
    void givenMerchandise_whenCreate_thenReturnsCreatedMerchandise() throws Exception {
        MerchandiseDto dto = new MerchandiseDto(null, "Poster", "Band Poster", 10, 50);

        String body = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/merchandise")
                .header(securityProperties.getAuthHeader(), adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name", is("Poster")))
            .andReturn();

        MerchandiseDto returned = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MerchandiseDto.class);
        assertEquals("Poster", returned.getName());
        assertEquals(10, returned.getPrice());
    }
}
