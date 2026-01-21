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
  reservedUntil?: string | null;
  reservedByUserId?: number | null;
  reservedByMe?: boolean;
  version: number;
}
