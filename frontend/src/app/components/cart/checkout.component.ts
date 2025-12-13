import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card>
      <mat-card-content>
        <h2>Checkout</h2>
        <p>Du bist eingeloggt. Hier kommt später "Order erstellen" usw.</p>
        <p>Items im Warenkorb: {{ cart.count() }}</p>
      </mat-card-content>
    </mat-card>
  `
})
export class CheckoutComponent {
  constructor(public cart: CartService) {}
}
