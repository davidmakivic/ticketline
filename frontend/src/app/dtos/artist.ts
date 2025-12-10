export interface Artist {
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
