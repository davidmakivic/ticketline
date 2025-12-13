package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;

public interface ArtistAutocompleteDto {
    Long getId();

    String getFirstName();

    String getLastName();

    String getStageName();

    ArtistType getArtistType();
}
