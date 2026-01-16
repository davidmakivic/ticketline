export interface MerchandiseDto {
  id: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
  imageContentType: string;
  variants: MerchandiseVariantDto[];
}

export interface MerchandiseVariantDto {
  id: number;
  size: string | null;
  quantity: number;
}
