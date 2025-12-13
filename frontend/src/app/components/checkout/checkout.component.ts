import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { CartService } from '../../services/cart.service';
import { OrdersService } from '../../services/order.service';

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
    {
      id: 'card',
      label: 'Kreditkarte',
      logo: '/assets/payments/visa-mastercard.svg'
    },
    {
      id: 'paypal',
      label: 'PayPal',
      logo: '/assets/payments/paypal.svg'
    },
    {
      id: 'klarna',
      label: 'Klarna',
      logo: '/assets/payments/klarna.svg'
    },
    {
      id: 'applepay',
      label: 'Apple Pay',
      logo: '/assets/payments/apple-pay.svg'
    }
  ];

  constructor(
    public cart: CartService,
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
