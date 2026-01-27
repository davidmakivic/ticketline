package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RewardDto;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.RewardServiceImpl;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards")
@Secured("ROLE_USER")
public class RewardEndpoint {

    private final RewardServiceImpl rewardService;

    public RewardEndpoint(RewardServiceImpl rewardService) {
        this.rewardService = rewardService;
    }

    @Secured({"ROLE_USER"})
    @GetMapping
    public List<RewardDto> getRewards() {
        return rewardService.getAllRewards();
    }
}
