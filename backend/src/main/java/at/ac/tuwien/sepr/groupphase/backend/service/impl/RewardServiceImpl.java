package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RewardDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RewardMapper;
import at.ac.tuwien.sepr.groupphase.backend.repository.RewardRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RewardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RewardMapper rewardMapper;

    public RewardServiceImpl(
        RewardRepository rewardRepository,
        RewardMapper rewardMapper
    ) {
        this.rewardRepository = rewardRepository;
        this.rewardMapper = rewardMapper;
    }

    @Transactional(readOnly = true)
    public List<RewardDto> getAllRewards() {
        return rewardMapper.rewardToRewardDtoList(
            rewardRepository.findAll()
        );
    }
}
