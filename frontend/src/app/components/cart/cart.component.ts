import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { Router } from '@angular/router';

import { CartService } from '../../services/cart.service';
import { TicketsService } from '../../services/tickets.service';
import { Ticket } from '../../dtos/ticket';
import { TicketCartItemComponent } from '../tickets/ticket-cart-item/ticket-cart-item.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    TicketCartItemComponent],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent {
  loading = false;
  tickets: Ticket[] = [];

  constructor(
    private cart: CartService,
    private ticketsService: TicketsService,
    private authService: AuthService,
    private router: Router
  ) {
    this.cart.cartItems$.subscribe(items => {
      this.loadTickets(items.map(i => i.ticketId));
    });
  }

  clear() {
    this.cart.clear();
  }

  removeTicket(ticket: Ticket) {
    this.cart.removeTicketAndRelease(ticket.id).subscribe({
      next: () => {},
      error: (e) => {
        console.error(e);
      }
    });
  }


  get totalPriceCents(): number {
    return this.tickets.reduce((sum, t) => sum + (t.priceFinalCents ?? 0), 0);
  }


  private loadTickets(ids: number[]) {
    if (ids.length === 0) {
      this.tickets = [];
      return;
    }

    this.loading = true;

    forkJoin(
      ids.map(id =>
        this.ticketsService.getTicketById(id).pipe(
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((t): t is Ticket => t !== null))
    ).subscribe({
      next: (tickets) => {
        const order = new Map(ids.map((id, idx) => [id, idx]));
        this.tickets = tickets.sort((a, b) => (order.get(a.id) ?? 0) - (order.get(b.id) ?? 0));
        this.loading = false;
      },
      error: () => {
        this.tickets = [];
        this.loading = false;
      }
    });
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
