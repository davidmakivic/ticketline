package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RewardDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RewardMapper.class})
public interface RewardMapper {

    @Mapping(source = "id", target = "rewardId")
    @Mapping(source = "pointsCost", target = "costInPoints")
    @Mapping(source = "merchandise", target = "merchandise")
    RewardDto rewardToRewardDto(Reward reward);

    List<RewardDto> rewardToRewardDtoList(List<Reward> rewards);


    default Page<RewardDto> rewardPageToRewardDtoPage(Page<Reward> rewards) {
        return rewards.map(this::rewardToRewardDto);
    }
}
