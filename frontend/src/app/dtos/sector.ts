import {TicketStatus} from "./ticket";

export enum SectorType {
  STANDING = 'STANDING',
  SEATED = 'SEATED',
  VIP = 'VIP'
}

export interface Sector {
  id: number;
  name: string;
  hallId: number;
  type: SectorType;
  priceCategoryId: number;
}
