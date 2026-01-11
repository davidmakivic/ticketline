export interface MerchandiseDto {
  id: number;
  name: string;
  description: string;
  price: number; // in cents
  quantity: number;
  imageContentType: string;
  availableSizes?: MerchandiseSize[];
}

export interface MerchandiseSize {
  size: string;      // z.B. "S", "M", "L"
  quantity: number;  // Lagerbestand pro Größe
}
