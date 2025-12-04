package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class VenueServiceTest {

    @Autowired
    VenueService venueService;

    @Autowired
    VenueRepository venueRepository;

    @BeforeEach
    public void beforeEach() {
        venueRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateVenue() {
        VenueDto v = new VenueDto();
        v.setName("Test Venue");
        v.setStreet("Test Street");
        v.setCity("Test City");
        v.setCountry("Test Country");
        v.setPostalCode("Test Postal Code");

        VenueDto saved = venueService.create(v);

        assertThat(saved.getName()).isEqualTo("Test Venue");
    }

    @Transactional
    @Test
    void testUpdateVenue() {
        VenueDto v = new VenueDto();
        v.setName("Test Venue");
        v.setStreet("Test Street");
        v.setCity("Test City");
        v.setCountry("Test Country");
        v.setPostalCode("Test Postal Code");

        VenueDto saved = venueService.create(v);
        Long id = saved.getId();

        VenueDto updated = new  VenueDto();
        updated.setName("Updated Venue");
        updated.setStreet("Updated Street");
        updated.setCity("Updated City");
        updated.setCountry("Updated Country");
        updated.setPostalCode("Updated Postal Code");

        VenueDto result = venueService.update(id, updated);

        assertThat(result.getName()).isEqualTo("Updated Venue");
        assertThat(result.getStreet()).isEqualTo("Updated Street");
    }

    @Transactional
    @Test
    void testUpdateVenueNotFound() {
        VenueDto v = new VenueDto();
        v.setName("Whatever");

        assertThatThrownBy(() -> venueService.update(999L, v))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAll() {
        VenueDto v1 = new VenueDto();
        v1.setName("Test Venue1");
        v1.setStreet("Test Street1");
        v1.setCity("Test City1");
        v1.setCountry("Test Country1");
        v1.setPostalCode("Test Postal Code1");

        VenueDto v2 = new VenueDto();
        v2.setName("Test Venue2");
        v2.setStreet("Test Street2");
        v2.setCity("Test City2");
        v2.setCountry("Test Country2");
        v2.setPostalCode("Test Postal Code2");

        venueService.create(v1);
        venueService.create(v2);

        List<VenueDto> result = venueService.findAll();

        assertThat(result).hasSize(2);
    }

    @Transactional
    @Test
    void testDeleteVenue() {
        VenueDto v = new VenueDto();
        v.setName("Test Venue");
        v.setStreet("Test Street");
        v.setCity("Test City");
        v.setCountry("Test Country");
        v.setPostalCode("Test Postal Code");

        VenueDto saved = venueService.create(v);
        Long id = saved.getId();

        assertThat(venueService.findById(id)).isNotNull();

        venueService.delete(id);

        assertThatThrownBy(() -> venueService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }
}
