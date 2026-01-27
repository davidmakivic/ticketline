import {MerchandiseDto} from "./merchandise";

export interface RewardDto {
  rewardId: number;
  costInPoints: number;
  merchandise: MerchandiseDto;
}
