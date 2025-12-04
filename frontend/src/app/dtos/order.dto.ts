export interface OrderDto {
  id: number;
  userId: number;
  totalPriceCents: number;
  createdAt: string;
  ticketIds: number[];
}
