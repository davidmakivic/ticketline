package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record ErrorListRestDto(
    String message,
    List<String> errors
) {
}
