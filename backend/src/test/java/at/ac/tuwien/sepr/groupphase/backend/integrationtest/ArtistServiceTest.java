package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
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
public class ArtistServiceTest {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateArtist() {
        Artist a = new Artist();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        Artist saved = artistService.create(a);

        assertThat(saved.getStageName()).isEqualTo("TestStage");
    }

    @Transactional
    @Test
    void testUpdateArtist() {
        Artist a = new Artist();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        Artist saved = artistService.create(a);
        Long id = saved.getId();

        a.setFirstName("Updated");
        a.setLastName("UpdatedLastName");
        a.setStageName("UpdatedStage");
        a.setArtistType(ArtistType.SOLO);

        Artist result = artistService.update(id, saved);

        assertThat(result.getStageName()).isEqualTo("UpdatedStage");
        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("UpdatedLastName");
    }

    @Transactional
    @Test
    void testUpdateArtistNotFound() {
        Artist a = new Artist();
        a.setFirstName("Test");

        assertThatThrownBy(() -> artistService.update(999L, a))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAllArtists() {
        Artist a = new Artist();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        Artist a2 = new Artist();
        a2.setFirstName("Test2");
        a2.setLastName("TestLastName2");
        a2.setStageName("TestStage2");
        a2.setArtistType(ArtistType.BAND);

        artistService.create(a);
        artistService.create(a2);

        List<Artist> result = artistService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testDeleteArtist() {
        Artist a = new Artist();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        Artist saved = artistService.create(a);
        Long id = saved.getId();

        assertThat(artistService.findById(id)).isNotNull();

        artistService.delete(id);

        assertThatThrownBy(() -> artistService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }
}
