export type SectorType = 'SEATED' | 'VIP' | 'STANDING';

export interface SectorIndexEntry {
  id: number;
  name: string;
  hallId: number;
  type: SectorType;
  priceCategoryId: number;
  sectorKey: string;
}

export type LayoutElement =
  | { type: 'stage'; id: string; label?: string; rectN?: { x:number; y:number; w:number; h:number } }
  | { type: 'standingArea'; id: string; label?: string; sectorKey: string; rectN?: { x:number; y:number; w:number; h:number } }
  | { type: 'seatBlock'; id: string; label?: string; sectorKey: string; rows: number; seatsPerRow: number;
  originN?: { x:number; y:number }; spacingN?: { dx:number; dy:number } };


export interface LayoutMetadata {
  version?: number;
  name?: string;
  aspect?: { w: number; h: number };
  elements: LayoutElement[];
}

export interface Hall {
  id: number;
  venueId: number;
  name: string;
  layoutMetadata: LayoutMetadata | null;
  sectorIndex: SectorIndexEntry[];
}
