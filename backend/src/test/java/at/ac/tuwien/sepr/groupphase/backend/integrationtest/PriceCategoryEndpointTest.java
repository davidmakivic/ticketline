package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    private static final String BASE_URL = "/api/v1/price-categories";

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }



    @Test
    void testGetById() throws Exception {
        PriceCategory priceCategory = new PriceCategory();
        priceCategory.setPrice(10.90);
        priceCategory.setName("C");
        PriceCategory saved = repository.save(priceCategory);

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
            .andExpect(jsonPath("$.name", is("C")))
            .andExpect(jsonPath("$.price", is(10.90)));
    }

    @Test
    void testGetAll() throws Exception {
        PriceCategory priceCategoryA = new PriceCategory();
        priceCategoryA.setPrice(10.90);
        priceCategoryA.setName("A");
        repository.save(priceCategoryA);
        PriceCategory priceCategoryB = new PriceCategory();
        priceCategoryB.setPrice(15.50);
        priceCategoryB.setName("B");
        repository.save(priceCategoryB);

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }



}
