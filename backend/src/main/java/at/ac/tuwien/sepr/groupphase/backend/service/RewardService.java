package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RewardDto;

import java.util.List;

public interface RewardService {
    public List<RewardDto> getAllRewards();
}
