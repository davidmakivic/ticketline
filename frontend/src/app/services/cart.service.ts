import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';

import { CartItem } from '../dtos/cart-item';
import { TicketsService } from './tickets.service';
import { Ticket } from '../dtos/ticket';

const STORAGE_KEY = 'cart.items.v1';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly items$ = new BehaviorSubject<CartItem[]>(this.load());
  readonly cartItems$ = this.items$.asObservable();

  constructor(private ticketsService: TicketsService) {}

  getCartItems(): CartItem[] {
    return this.getItems();
  }

  getItems(): CartItem[] {
    return this.items$.value;
  }

  getTotalCents(ticketById?: Map<number, { priceFinalCents?: number }>): number {
    if (!ticketById) return 0;

    return this.getItems().reduce((sum, i) => {
      const t = ticketById.get(i.ticketId);
      return sum + (t?.priceFinalCents ?? 0);
    }, 0);
  }
  addTicket(ticketId: number): void {
    const current = this.items$.value;
    if (current.some(i => i.ticketId === ticketId)) return;
    this.set([...current, { ticketId, addedAt: new Date().toISOString() }]);
  }

  addTicketAndReserve(ticketId: number): Observable<Ticket> {
    return this.ticketsService.getTicketById(ticketId).pipe(
      switchMap((ticket: Ticket) => this.ticketsService.reserve(ticketId, ticket.version)),
      tap(() => this.addTicket(ticketId))
    );
  }

  removeTicketAndRelease(ticketId: number) {
    return this.ticketsService.getTicketById(ticketId).pipe(
      switchMap(ticket => this.ticketsService.release(ticketId, ticket.version)),
      tap(() => this.removeTicket(ticketId))
    );
  }

  removeTicket(ticketId: number): void {
    this.set(this.items$.value.filter(i => i.ticketId !== ticketId));
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
