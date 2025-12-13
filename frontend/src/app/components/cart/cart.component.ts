import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent {

  constructor(
    public cart: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }


  onBuy(): void {
    if (this.cart.getCartItems().length === 0) {
      return;
    }

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], {
        queryParams: { redirect: '/checkout' }
      });
      return;
    }


    this.router.navigate(['/checkout']);
  }
}
