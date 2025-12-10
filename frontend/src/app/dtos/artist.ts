import {EventDto} from "./event";

export interface Artist {
  id: number;
  firstName: string;
  lastName: string;
  stageName: string;
  artistType: ArtistType;
  events: EventDto[];
}

export enum ArtistType {
  SOLO = 'SOLO',
  BAND = 'BAND',
}
