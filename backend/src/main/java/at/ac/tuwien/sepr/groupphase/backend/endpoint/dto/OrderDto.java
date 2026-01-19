package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDto {

    private Long id;
    private Long userId;
    private Long totalPriceCents;
    private Instant createdAt;
    private List<Long> ticketIds = new ArrayList<>();
    private List<OrderMerchItemDto> merchItems = new ArrayList<>();

    public OrderDto() {
    }

    public OrderDto(Long id,
                    Long userId,
                    Long totalPriceCents,
                    Instant createdAt,
                    List<Long> ticketIds) {
        this(id, userId, totalPriceCents, createdAt, ticketIds, List.of());
    }

    public OrderDto(Long id,
                    Long userId,
                    Long totalPriceCents,
                    Instant createdAt,
                    List<Long> ticketIds,
                    List<OrderMerchItemDto> merchItems) {
        this.id = id;
        this.userId = userId;
        this.totalPriceCents = totalPriceCents;
        this.createdAt = createdAt;
        this.ticketIds = ticketIds != null ? ticketIds : List.of();
        this.merchItems = merchItems != null ? merchItems : List.of();
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

    public List<OrderMerchItemDto> getMerchItems() {
        return merchItems;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTotalPriceCents(Long totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setTicketIds(List<Long> ticketIds) {
        this.ticketIds = ticketIds != null ? ticketIds : List.of();
    }

    public void setMerchItems(List<OrderMerchItemDto> merchItems) {
        this.merchItems = merchItems != null ? merchItems : List.of();
    }

    @Override
    public String toString() {
        return "OrderDto{"
            + "id=" + id
            + ", userId=" + userId
            + ", totalPriceCents=" + totalPriceCents
            + ", createdAt=" + createdAt
            + ", ticketIds=" + ticketIds
            + ", merchItems=" + merchItems
            + '}';
    }
}
