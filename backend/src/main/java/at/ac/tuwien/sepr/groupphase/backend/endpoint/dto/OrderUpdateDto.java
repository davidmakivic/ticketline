package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public class OrderUpdateDto {

    private List<Long> ticketIds;
    private List<OrderMerchItemCreateDto> merchItems;

    public OrderUpdateDto() {
    }

    public OrderUpdateDto(List<Long> ticketIds, List<OrderMerchItemCreateDto> merchItems) {
        this.ticketIds = ticketIds;
        this.merchItems = merchItems;
    }

    public List<Long> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(List<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public List<OrderMerchItemCreateDto> getMerchItems() {
        return merchItems;
    }

    public void setMerchItems(List<OrderMerchItemCreateDto> merchItems) {
        this.merchItems = merchItems;
    }
}
