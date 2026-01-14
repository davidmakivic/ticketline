export interface MerchandiseDto {
  id: number;
  name: string;
  description: string;
  price: number; // in cents
  quantity: number; // Gesamtmenge (Backend summiert Varianten)
  imageContentType: string;
  variants: MerchandiseVariantDto[];
}

export interface MerchandiseVariantDto {
  size: string | null;
  quantity: number;
}
