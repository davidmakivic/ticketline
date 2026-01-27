export type CartItem = TicketCartItem | MerchCartItem | RewardCartItem;

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

export interface RewardCartItem {
  kind: 'reward';
  merchandiseId: number;
  variantId: number;
  name: string;
  size: string | null;
  unitPricePoints: number;
  quantity: number;
  addedAt: string;
}

export function isRewardItem(x: any): x is RewardCartItem {
  return x && (x.kind === 'reward');
}

export function isMerchItem(x: any): x is MerchCartItem {
  return x && (x.kind === 'merch');
}

export function isTicketItem(x: any): x is TicketCartItem {
  return x && !isMerchItem(x) && x.ticketId != null;
}

function emphasizeMerchShape(x: any): boolean {
  return ('merchandiseId' in x) || ('name' in x) || ('unitPriceCents' in x) || ('quantity' in x);
}
