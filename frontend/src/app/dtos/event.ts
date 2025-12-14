import { Artist } from './artist';
import { Performance } from './performance';
import {SafeUrl} from "@angular/platform-browser";

export interface EventDto {
  id: number;
  title: string;
  description: string;
  category: EventType;
  durationMinutes: number;
  artists: Artist[];
  performances: Performance[];
}

export interface EventAutocompleteDto {
  id: number;
  title: string;
  image?: string;
}

export interface EventTop10Dto {
  eventId: number;
  title: string;
  soldTickets: number;
  imageUrl?: SafeUrl;
}


export enum EventType {
  CONCERT = 'CONCERT',
  FESTIVAL = 'FESTIVAL',
  }
export interface TopEvent {
  title: string,
  soldTickets: number;
}

