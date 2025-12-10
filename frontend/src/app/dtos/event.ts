import { Artist } from './artist';
import { Performance } from './performance';

export interface EventDto {
  id: number;
  title: string;
  description: string;
  category: EventType;
  durationMinutes: number;
  artists: Artist[];
  performances: Performance[];
}

export enum EventType {
  CONCERT = 'CONCERT',
  FESTIVAL = 'FESTIVAL',
  }
export interface TopEvent {
  title: string,
  soldTickets: number;
}

