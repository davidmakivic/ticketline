import { Artist } from './artist';
import { Performance } from './performance';
import {SafeUrl} from "@angular/platform-browser";

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

export interface EventTop10Dto {
  eventId: number;
  title: string;
  soldTickets: number;
  imageUrl?: SafeUrl;
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

