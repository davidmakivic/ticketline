package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;
import java.util.List;

public class CancellationResultDto {

    private final Long orderId;
    private final List<Long> cancelledTicketIds;
    private final Long refundTotalCents;
    private final Instant createdAt;

    public CancellationResultDto(Long orderId, List<Long> cancelledTicketIds, Long refundTotalCents, Instant createdAt) {
        this.orderId = orderId;
        this.cancelledTicketIds = cancelledTicketIds;
        this.refundTotalCents = refundTotalCents;
        this.createdAt = createdAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<Long> getCancelledTicketIds() {
        return cancelledTicketIds;
    }

    public Long getRefundTotalCents() {
        return refundTotalCents;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
