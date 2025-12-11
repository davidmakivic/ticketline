import { TicketStatus } from './ticket';

export interface TicketStatusUpdateDto {
  status: TicketStatus;
  version: number;
}
