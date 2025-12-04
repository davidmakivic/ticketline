package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CheckoutRequest(
    @NotEmpty List<Long> ticketIds
) {}