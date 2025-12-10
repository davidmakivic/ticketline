package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;
import java.util.List;

public class OrderDto {

    private final Long id;
    private final Long userId;
    private final Long totalPriceCents;
    private final Instant createdAt;
    private final List<Long> ticketIds;

    public OrderDto(Long id,
                    Long userId,
                    Long totalPriceCents,
                    Instant createdAt,
                    List<Long> ticketIds) {
        this.id = id;
        this.userId = userId;
        this.totalPriceCents = totalPriceCents;
        this.createdAt = createdAt;
        this.ticketIds = ticketIds;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTotalPriceCents() {
        return totalPriceCents;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Long> getTicketIds() {
        return ticketIds;
    }

    @Override
    public String toString() {
        return "OrderDto{"
            +
            "id=" + id
            +
            ", userId=" + userId
            +
            ", totalPriceCents=" + totalPriceCents
            +
            ", createdAt=" + createdAt
            +
            ", ticketIds=" + ticketIds
            + '}';
    }
}
