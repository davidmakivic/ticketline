export interface OrderMerchItemDto {
  variantId: number;
  merchandiseId: number;
  merchandiseName: string;
  size: string | null;
  quantity: number;
  unitPriceCents: number;
}

export interface OrderDto {
  id: number;
  userId: number;
  totalPriceCents: number;
  createdAt: string;
  ticketIds: number[];
  merchItems: OrderMerchItemDto[];
}
