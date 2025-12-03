package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;

public class ArtistTestDataFactory {
    public static ArtistDto create(ArtistType type, String stageName) {
        ArtistDto artist = new ArtistDto();
        artist.setFirstName("Test");
        artist.setLastName("Test");
        artist.setStageName(stageName);
        artist.setArtistType(type);
        return artist;
    }
}
