package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    @Autowired
    private EventService eventService;

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateArtist() {
        ArtistDto a = new ArtistDto();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        ArtistDto saved = artistService.create(a);

        assertThat(saved.getStageName()).isEqualTo("TestStage");
    }

    @Transactional
    @Test
    void testUpdateArtist() {
        ArtistDto a = new ArtistDto();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        ArtistDto saved = artistService.create(a);
        Long id = saved.getId();

        a.setFirstName("Updated");
        a.setLastName("UpdatedLastName");
        a.setStageName("UpdatedStage");
        a.setArtistType(ArtistType.SOLO);

        ArtistDto result = artistService.update(id, a);

        assertThat(result.getStageName()).isEqualTo("UpdatedStage");
        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("UpdatedLastName");
    }

    @Transactional
    @Test
    void testUpdateArtistNotFound() {
        ArtistDto a = new ArtistDto();
        a.setFirstName("Test");

        assertThatThrownBy(() -> artistService.update(999L, a))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAllArtists() {
        ArtistDto a = new ArtistDto();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        ArtistDto a2 = new ArtistDto();
        a2.setFirstName("Test2");
        a2.setLastName("TestLastName2");
        a2.setStageName("TestStage2");
        a2.setArtistType(ArtistType.BAND);

        artistService.create(a);
        artistService.create(a2);

        List<ArtistDto> result = artistService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testDeleteArtist() {
        ArtistDto a = new ArtistDto();
        a.setFirstName("Test");
        a.setLastName("TestLastName");
        a.setStageName("TestStage");
        a.setArtistType(ArtistType.SOLO);

        ArtistDto saved = artistService.create(a);
        Long id = saved.getId();

        assertThat(artistService.findById(id)).isNotNull();

        artistService.delete(id);

        assertThatThrownBy(() -> artistService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testAddEventToArtist() throws IOException {
        ArtistDto artist = new ArtistDto();
        artist.setFirstName("A");
        artist.setLastName("B");
        artist.setStageName("TestStage");
        artist.setArtistType(ArtistType.SOLO);
        ArtistDto savedArtist = artistService.create(artist);

        EventDto savedEvent = eventService.create("My Event", "desc", EventType.CONCERT, 90, null);

        artistService.addEvent(savedArtist.getId(), savedEvent.getId());

        ArtistDto updated = artistService.findById(savedArtist.getId());
        assertThat(updated.getEvents()).hasSize(1);
    }

    @Transactional
    @Test
    void testRemoveEventFromArtist() throws IOException {
        ArtistDto artist = new ArtistDto();
        artist.setFirstName("A");
        artist.setLastName("B");
        artist.setStageName("TestStage");
        artist.setArtistType(ArtistType.SOLO);
        ArtistDto savedArtist = artistService.create(artist);

        EventDto savedEvent = eventService.create("My Event", "desc", EventType.MUSICAL, 90, null);

        artistService.addEvent(savedArtist.getId(), savedEvent.getId());

        artistService.deleteEvent(savedArtist.getId(), savedEvent.getId());

        ArtistDto updated = artistService.findById(savedArtist.getId());
        assertThat(updated.getEvents()).isEmpty();
    }

    @Transactional
    @Test
    void testFindByNamePositive() {
        ArtistDto artist1 = new ArtistDto();
        artist1.setFirstName("David");
        artist1.setLastName("Bowie");
        artist1.setStageName("Ziggy");
        artist1.setArtistType(ArtistType.SOLO);
        artistService.create(artist1);

        ArtistDto artist2 = new ArtistDto();
        artist2.setFirstName("John");
        artist2.setLastName("Lennon");
        artist2.setStageName("Beatles");
        artist2.setArtistType(ArtistType.BAND);
        artistService.create(artist2);

        List<ArtistDto> result = artistService.findByName("ziggy");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStageName()).isEqualTo("Ziggy");
    }

    @Transactional
    @Test
    void testFindByNameNegative() {
        ArtistDto artist = new ArtistDto();
        artist.setFirstName("Test");
        artist.setLastName("Artist");
        artist.setStageName("TestStage");
        artist.setArtistType(ArtistType.SOLO);
        artistService.create(artist);

        List<ArtistDto> result = artistService.findByName("nonexistent");

        assertThat(result).isEmpty();
    }


}
