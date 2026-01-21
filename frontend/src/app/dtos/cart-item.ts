export type CartItem = TicketCartItem | MerchCartItem;

export interface TicketCartItem {
  kind?: 'ticket';
  ticketId: number;
  addedAt: string;
}

export interface MerchCartItem {
  kind: 'merch';
  merchandiseId: number;
  variantId: number;
  name: string;
  size: string | null;
  unitPriceCents: number;
  quantity: number;
  addedAt: string;
}

export function isMerchItem(x: any): x is MerchCartItem {
  return x && (x.kind === 'merch' || (x.variantId != null && emphasizeMerchShape(x)));
}

export function isTicketItem(x: any): x is TicketCartItem {
  return x && !isMerchItem(x) && x.ticketId != null;
}

function emphasizeMerchShape(x: any): boolean {
  return ('merchandiseId' in x) || ('name' in x) || ('unitPriceCents' in x) || ('quantity' in x);
}
