package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;

public class VenueTestDataFactory {
    public static VenueDto create() {
        VenueDto venue = new VenueDto();
        venue.setName("Test Name");
        venue.setStreet("Test Street");
        venue.setCity("Test City");
        venue.setCountry("Test Country");
        venue.setPostalCode("Test Postal Code");

        return venue;
    }
}
