import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, mapTo, tap } from 'rxjs/operators';

import { CartItem, MerchCartItem, TicketCartItem, isMerchItem, isTicketItem } from '../dtos/cart-item';
import { TicketsService } from './tickets.service';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly key = 'ticketline.cart.v2';

  private items: CartItem[] = [];
  private readonly subject = new BehaviorSubject<CartItem[]>([]);
  readonly cartItems$ = this.subject.asObservable();

  constructor(private ticketsService: TicketsService) {
    this.items = this.load();
    this.subject.next([...this.items]);
  }

  addTicket(ticketId: number): void {
    if (!Number.isFinite(ticketId)) return;
    const exists = this.items.some(i => isTicketItem(i) && i.ticketId === ticketId);
    if (exists) return;

    const it: TicketCartItem = { kind: 'ticket', ticketId, addedAt: new Date().toISOString() };
    this.items = [...this.items, it];
    this.persistEmit();
  }

  removeTicket(ticketId: number): void {
    this.items = this.items.filter(i => !(isTicketItem(i) && i.ticketId === ticketId));
    this.persistEmit();
  }

  removeTicketAndRelease(ticketId: number): Observable<void> {
    this.removeTicket(ticketId);
    return this.ticketsService.releaseHold(ticketId).pipe(
      catchError(() => of(null)),
      mapTo(void 0)
    );
  }

  addMerch(item: Omit<MerchCartItem, 'addedAt' | 'kind'>): void {
    if (!item || !Number.isFinite(item.variantId) || !Number.isFinite(item.quantity) || item.quantity <= 0) return;

    const now = new Date().toISOString();

    const idx = this.items.findIndex(i => isMerchItem(i) && i.variantId === item.variantId);
    if (idx >= 0) {
      const cur = this.items[idx] as MerchCartItem;
      const updated: MerchCartItem = { ...cur, quantity: cur.quantity + item.quantity };
      this.items = this.items.map((x, i) => (i === idx ? updated : x));
      this.persistEmit();
      return;
    }

    const it: MerchCartItem = {
      kind: 'merch',
      addedAt: now,
      ...item
    };

    this.items = [...this.items, it];
    this.persistEmit();
  }

  removeMerch(variantId: number): void {
    this.items = this.items.filter(i => !(isMerchItem(i) && i.variantId === variantId));
    this.persistEmit();
  }

  updateMerchQuantity(variantId: number, quantity: number): void {
    if (!Number.isFinite(quantity) || quantity <= 0) {
      this.removeMerch(variantId);
      return;
    }
    this.items = this.items.map(i => {
      if (isMerchItem(i) && i.variantId === variantId) return { ...i, quantity };
      return i;
    });
    this.persistEmit();
  }

  updateMerchVariant(oldVariantId: number, next: { variantId: number; size: string | null }): void {
    const cur = this.items.find(i => isMerchItem(i) && i.variantId === oldVariantId) as MerchCartItem | undefined;
    if (!cur) return;

    const targetIdx = this.items.findIndex(i => isMerchItem(i) && i.variantId === next.variantId);
    if (targetIdx >= 0) {
      const target = this.items[targetIdx] as MerchCartItem;
      const mergedQty = target.quantity + cur.quantity;

      this.items = this.items
        .filter(i => !(isMerchItem(i) && i.variantId === oldVariantId))
        .map((i, idx) => (idx === targetIdx ? { ...target, quantity: mergedQty } : i));

      this.persistEmit();
      return;
    }

    this.items = this.items.map(i => {
      if (isMerchItem(i) && i.variantId === oldVariantId) {
        return { ...i, variantId: next.variantId, size: next.size };
      }
      return i;
    });
    this.persistEmit();
  }

  getCartItems(): CartItem[] {
    return [...this.items];
  }

  clear(): void {
    this.items = [];
    this.persistEmit();
  }

  private persistEmit(): void {
    try {
      localStorage.setItem(this.key, JSON.stringify(this.items));
    } catch {}
    this.subject.next([...this.items]);
  }

  private load(): CartItem[] {
    try {
      const raw = localStorage.getItem(this.key);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed.map((x: any) => {
        if (isMerchItem(x)) {
          return {
            kind: 'merch',
            merchandiseId: Number(x.merchandiseId),
            variantId: Number(x.variantId),
            name: String(x.name ?? ''),
            size: x.size ?? null,
            unitPriceCents: Number(x.unitPriceCents ?? 0),
            quantity: Number(x.quantity ?? 1),
            addedAt: String(x.addedAt ?? new Date().toISOString())
          } as MerchCartItem;
        }
        return {
          kind: 'ticket',
          ticketId: Number(x.ticketId),
          addedAt: String(x.addedAt ?? new Date().toISOString())
        } as TicketCartItem;
      }).filter(x => (isMerchItem(x) ? Number.isFinite(x.variantId) : Number.isFinite((x as any).ticketId)));
    } catch {
      return [];
    }
  }


   addTicketAndHold(ticketId: number): Observable<void> {

     this.addTicket(ticketId);
     return of(void 0);
   }

}
