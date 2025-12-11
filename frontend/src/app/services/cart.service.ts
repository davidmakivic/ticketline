import { Injectable } from '@angular/core';
import { CartItem } from '../dtos/cart-item';

@Injectable({ providedIn: 'root' })
export class CartService {

  private items: CartItem[] = [];

  getItems(): CartItem[] {
    return this.items;
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

  getReservedItems(): CartItem[] {
    return this.items.filter(i => i.reserved);
  }

  getCartItems(): CartItem[] {
    return this.items.filter(i => !i.reserved);
  }
}
