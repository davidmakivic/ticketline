import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem } from '../dtos/cart-item';

const STORAGE_KEY = 'cart.items.v1';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly items$ = new BehaviorSubject<CartItem[]>(this.load());

  /** Observable für UI */
  readonly cartItems$ = this.items$.asObservable();

  /** Sync getter (z.B. für Guards/Checks) */
  getItems(): CartItem[] {
    return this.items$.value;
  }

  addTicket(ticketId: number): void {
    const current = this.items$.value;

    // Du willst wahrscheinlich keine doppelten Tickets im Cart
    if (current.some(i => i.ticketId === ticketId)) return;

    const next = [...current, { ticketId, addedAt: new Date().toISOString() }];
    this.set(next);
  }

  removeTicket(ticketId: number): void {
    const next = this.items$.value.filter(i => i.ticketId !== ticketId);
    this.set(next);
  }

  clear(): void {
    this.set([]);
  }

  count(): number {
    return this.items$.value.length;
  }

  private set(items: CartItem[]): void {
    this.items$.next(items);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  }

  private load(): CartItem[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) return [];
      const parsed = JSON.parse(raw) as CartItem[];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }
}
