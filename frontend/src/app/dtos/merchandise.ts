export interface MerchandiseDto {
  id: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
  imageContentType: string;
  variants: MerchandiseVariantDto[];
}

export enum MerchandiseSize {
  XS = 'XS',
  S = 'S',
  M = 'M',
  L = 'L',
  XL = 'XL',
}

export interface MerchandiseVariantDto {
  id: number;
  size: MerchandiseSize | null;
  quantity: number;
}
