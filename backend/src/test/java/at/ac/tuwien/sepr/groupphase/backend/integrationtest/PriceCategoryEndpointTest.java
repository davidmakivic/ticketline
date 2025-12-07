package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PriceCategoryEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PriceCategoryRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/price-categories";

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }



    @Test
    void testGetById() throws Exception {
        PriceCategory saved = repository.save(new PriceCategory("A", 10.90));

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
            .andExpect(jsonPath("$.priceCategory", is("A")))
            .andExpect(jsonPath("$.price", is(10.90)));
    }

    @Test
    void testGetAll() throws Exception {
        repository.save(new PriceCategory("A", 10.90));
        repository.save(new PriceCategory("B", 15.50));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }



}
