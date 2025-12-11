export interface CartItem {
  id: number;
  type: 'TICKET' | 'MERCH';
  title: string;
  subtitle?: string;
  date?: string;
  time?: string;
  location?: string;
  quantity: number;
  priceCents: number;
  imageUrl: string;
  reserved?: boolean;
}
