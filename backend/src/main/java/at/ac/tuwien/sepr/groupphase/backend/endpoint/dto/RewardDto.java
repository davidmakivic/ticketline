package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class RewardDto {
    private Long rewardId;
    private MerchandiseDto merchandise;
    private Long costInPoints;

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public MerchandiseDto getMerchandise() {
        return merchandise;
    }

    public void setMerchandise(MerchandiseDto merchandise) {
        this.merchandise = merchandise;
    }

    public Long getCostInPoints() {
        return costInPoints;
    }

    public void setCostInPoints(Long costInPoints) {
        this.costInPoints = costInPoints;
    }
}
