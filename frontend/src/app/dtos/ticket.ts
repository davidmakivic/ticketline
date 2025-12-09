export enum TicketStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  PURCHASED = 'PURCHASED'
}

export interface Ticket {
  id: number;
  performanceId: number;
  seatId: number | null;
  priceFinalCents: number;
  status: TicketStatus;
  version: number;
}
