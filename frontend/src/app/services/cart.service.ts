import { Injectable } from '@angular/core';
import { CartItem } from '../dtos/cart-item';

@Injectable({ providedIn: 'root' })
export class CartService {

  private items: CartItem[] = [];

  getItems(): CartItem[] {
    return this.items;
  }

  getCartItems(): CartItem[] {
    return this.items.filter(i => !i.reserved);
  }

  getReservedItems(): CartItem[] {
    return this.items.filter(i => i.reserved);
  }

  addItem(item: CartItem) {
    this.items.push(item);
  }

  removeItem(id: number) {
    this.items = this.items.filter(i => i.id !== id);
  }

  clear() {
    this.items = [];
  }

  getTotalCents(): number {
    return this.items
      .filter(i => !i.reserved)
      .reduce((sum, item) => sum + item.priceCents * item.quantity, 0);
  }

  reserveItem(id: number) {
    const item = this.items.find(i => i.id === id);
    if (item) {
      item.reserved = true;
      item.reservedUntil = Date.now() + 15 * 60 * 1000;
    }
  }

  addTicketToCart(ticket: {
    id: number;
    title: string;
    subtitle?: string;
    date?: string;
    time?: string;
    location?: string;
    priceCents: number;
    imageUrl: string;
  }) {
    this.items.push({
      ...ticket,
      type: 'TICKET',
      quantity: 1,
      reserved: false
    });
  }
}
