import { Artist } from './artist';
import { PerformanceDto } from './performanceDto';
import {SafeUrl} from "@angular/platform-browser";

export interface EventDto {
  id: number;
  title: string;
  description: string;
  category: EventTypeDto;
  durationMinutes: number;
  artists: Artist[];
  performances: PerformanceDto[];
}

export interface PagedResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  empty: boolean;
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

export interface SimpleEventDto {
  id: number;
  title: string;
}

