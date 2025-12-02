export enum TicketStatus {
  RESERVED = 'RESERVED',
  PURCHASED = 'PURCHASED',
  CANCELLED = 'CANCELLED'
}

export interface Ticket {
  id: number;
  performanceId: number;
  seatId: number;
  orderId: number;
  priceFinalCents: number;
  status: TicketStatus;
}
