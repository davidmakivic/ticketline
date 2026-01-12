import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CartService } from '../services/cart.service';

@Injectable({ providedIn: 'root' })
export class CheckoutGuard {
  constructor(
    private auth: AuthService,
    private cart: CartService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.cart.count() === 0) {
      this.router.navigate(['/cart']);
      return false;
    }

    if (!this.auth.isLoggedIn()) {
      // optional: redirect-URL merken
      sessionStorage.setItem('postLoginRedirect', '/cart/checkout');
      this.router.navigate(['/login']);
      return false;
    }

    return true;
  }
}
