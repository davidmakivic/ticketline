import { Artist } from './artist';
import { Performance } from './performance';

export interface EventDto {
  id: number;
  title: string;
  description: string;
  category: EventTypeDto;
  durationMinutes: number;
  artists: Artist[];
  performances: Performance[];
}

export interface EventAutocompleteDto {
  id: number;
  title: string;
  image?: string;
}

export enum EventTypeDto {
  CONCERT = 'CONCERT',
  FESTIVAL = 'FESTIVAL',
  MUSICAL = 'MUSICAL',
  }
export interface TopEvent {
  title: string,
  soldTickets: number;
}

