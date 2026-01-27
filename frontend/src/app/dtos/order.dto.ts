export interface OrderMerchItemDto {
  variantId: number;
  merchandiseId: number;
  merchandiseName: string;
  size: string | null;
  quantity: number;
  unitPriceCents: number;
}

export interface OrderRewardItemDto {
  rewardId: number;
  variantId: number;
  merchandiseName: string;
  quantity: number;
  unitPricePoints: number;
}

export interface OrderDto {
  id: number;
  userId: number;
  totalPriceCents: number;
  createdAt: string;
  ticketIds: number[];
  merchItems: OrderMerchItemDto[];
  rewardItems: OrderRewardItemDto[];
  usedRewardPoints: number;
}
