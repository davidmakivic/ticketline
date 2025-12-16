package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;

import java.util.HashSet;

public class ArtistTestDataFactory {
    public static ArtistDto create(String stageName, ArtistType type) {
        ArtistDto dto = new ArtistDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setStageName(stageName);
        dto.setArtistType(type);
        dto.setEvents(new HashSet<>());
        return dto;
    }

    public static ArtistDto create() {
        return create("TestArtist", ArtistType.SOLO);
    }
}