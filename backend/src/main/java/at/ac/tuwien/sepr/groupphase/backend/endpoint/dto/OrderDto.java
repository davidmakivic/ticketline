package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDto {

    private Long id;
    private Long userId;
    private Long totalPriceCents;
    private Long totalPricePoints;
    private Instant createdAt;
    private List<Long> ticketIds = new ArrayList<>();
    private List<OrderMerchItemDto> merchItems = new ArrayList<>();
    private List<OrderRewardItemDto> rewardItems = new ArrayList<>();

    public OrderDto() {
    }

    public OrderDto(Long id,
                    Long userId,
                    Long totalPriceCents,
                    Long totalPricePoints,
                    Instant createdAt,
                    List<Long> ticketIds) {
        this(id, userId, totalPriceCents, totalPricePoints, createdAt, ticketIds, List.of(), List.of());
    }

    public OrderDto(Long id,
                    Long userId,
                    Long totalPriceCents,
                    Long totalPricePoints,
                    Instant createdAt,
                    List<Long> ticketIds,
                    List<OrderMerchItemDto> merchItems,
                    List<OrderRewardItemDto> rewardItems) {
        this.id = id;
        this.userId = userId;
        this.totalPriceCents = totalPriceCents;
        this.totalPricePoints = totalPricePoints;
        this.createdAt = createdAt;
        this.ticketIds = ticketIds != null ? ticketIds : List.of();
        this.merchItems = merchItems != null ? merchItems : List.of();
        this.rewardItems = rewardItems != null ? rewardItems : List.of();
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

    public List<OrderRewardItemDto> getRewardItems() {
        return rewardItems;
    }

    public Long getTotalPricePoints() {
        return totalPricePoints;
    }

    public void setTotalPricePoints(Long totalPricePoints) {
        this.totalPricePoints = totalPricePoints;
    }

    public void setRewardItems(List<OrderRewardItemDto> rewardItems) {
        this.rewardItems = rewardItems;
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
