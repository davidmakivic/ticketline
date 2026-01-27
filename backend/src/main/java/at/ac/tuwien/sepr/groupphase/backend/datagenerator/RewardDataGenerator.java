package at.ac.tuwien.sepr.groupphase.backend.datagenerator;


import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reward;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RewardRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("generateData")
@DependsOn({"merchandiseDataGenerator"})
@Component
public class RewardDataGenerator {

    private final RewardRepository rewardRepository;
    private final MerchandiseRepository merchandiseRepository;

    public RewardDataGenerator(
        RewardRepository rewardRepository,
        MerchandiseRepository merchandiseRepository
    ) {
        this.rewardRepository = rewardRepository;
        this.merchandiseRepository = merchandiseRepository;
    }

    @PostConstruct
    public void generateRewards() {

        if (rewardRepository.count() > 0) {
            return;
        }

        List<Merchandise> merch = merchandiseRepository.findAll();

        merch.stream()
            .filter(m -> m.getPrice() != null && m.getPrice() > 0)
            .limit(3) // z. B. nur 3 Prämien
            .forEach(m -> {
                Reward reward = new Reward();
                reward.setMerchandise(m);
                reward.setPointsCost(calculatePoints(m));

                rewardRepository.save(reward);
            });
    }

    private Long calculatePoints(Merchandise merch) {
        // einfache Regel: 1 Punkt = 10 €
        return (long) merch.getPrice() / 10;
    }
}
