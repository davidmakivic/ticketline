import {EventDto} from "./event";

export interface Artist {
  id: number;
  firstName: string;
  lastName: string;
  stageName: string;
  artistType: ArtistType;
  events: EventDto[];
}

export interface ArtistAutocompleteDto {
  id: number;
  firstName: string;
  lastName: string;
  stageName: string;
  artistType: ArtistType;
  image?: string;
}

export interface ArtistDataDto {
  id: number;
  firstName: string;
  lastName: string;
  stageName: string;
  artistType: ArtistType;
}

export enum ArtistType {
  SOLO = 'SOLO',
  BAND = 'BAND',
}
