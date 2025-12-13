import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { CartService } from '../../services/cart.service';
import { OrdersService } from '../../services/order.service';
import { TicketsService } from '../../services/tickets.service';
import { Ticket } from '../../dtos/ticket';

import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

type PaymentId = 'card' | 'paypal' | 'klarna' | 'applepay';

interface PaymentMethod {
  id: PaymentId;
  label: string;
  logo: string;
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent {

  selectedPayment: PaymentId = 'card';

  paymentMethods: PaymentMethod[] = [
    { id: 'card', label: 'Kreditkarte', logo: '/assets/payments/visa-mastercard.svg' },
    { id: 'paypal', label: 'PayPal', logo: '/assets/payments/paypal.svg' },
    { id: 'klarna', label: 'Klarna', logo: '/assets/payments/klarna.svg' },
    { id: 'applepay', label: 'Apple Pay', logo: '/assets/payments/apple-pay.svg' }
  ];

  readonly totalCents$ = this.cart.cartItems$.pipe(
    map(items => items.map(i => i.ticketId)),
    switchMap(ids => {
      if (ids.length === 0) return of([] as Ticket[]);
      return forkJoin(
        ids.map(id =>
          this.ticketsService.getTicketById(id).pipe(
            catchError(() => of(null))
          )
        )
      ).pipe(
        map(list => list.filter((t): t is Ticket => t !== null))
      );
    }),
    map(tickets =>
      tickets.reduce((sum, t) => sum + (t.priceFinalCents ?? 0), 0)
    )
  );

  constructor(
    public cart: CartService,
    private ticketsService: TicketsService,
    private orders: OrdersService,
    private router: Router,
  ) {}

  select(method: PaymentId): void {
    this.selectedPayment = method;
  }

  pay(): void {
    const items = this.cart.getCartItems();
    if (items.length === 0) {
      alert('Warenkorb ist leer');
      return;
    }

    const boughtItems = items.map(i => ({ ...i }));

    this.orders.createFromCart(items).subscribe({
      next: (order) => {
        this.cart.clear();
        this.router.navigate(['/invoice', order.id], {
          state: {
            order,
            items: boughtItems,
            payment: this.selectedPayment
          }
        });
      },
      error: err => {
        console.error(err);
        alert('Bestellung fehlgeschlagen');
      }
    });
  }

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }
}
