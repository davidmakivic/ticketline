import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { CartService } from '../../services/cart.service';
import { TicketsService } from '../../services/tickets.service';
import { Ticket } from '../../dtos/ticket';
import { TicketCartItemComponent } from '../tickets/ticket-cart-item/ticket-cart-item.component';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, TicketCartItemComponent],
  templateUrl: './cart.component.html',
})
export class CartComponent {
  loading = false;

  // hier liegen dann die echten Tickets für die UI
  tickets: Ticket[] = [];

  constructor(private cart: CartService, private ticketsService: TicketsService) {
    this.cart.cartItems$.subscribe(items => {
      this.loadTickets(items.map(i => i.ticketId));
    });
  }

  clear() {
    this.cart.clear();
  }

  removeTicket(ticket: Ticket) {
    this.cart.removeTicket(ticket.id);
  }

  private loadTickets(ids: number[]) {
    if (ids.length === 0) {
      this.tickets = [];
      return;
    }

    this.loading = true;

    // Für jede ID Ticket laden (parallel)
    forkJoin(
      ids.map(id =>
        this.ticketsService.getTicketById(id).pipe(
          catchError(() => of(null)) // wenn ein Ticket nicht ladbar ist, ignorieren
        )
      )
    ).pipe(
      map(list => list.filter((t): t is Ticket => t !== null))
    ).subscribe({
      next: (tickets) => {
        // sortiere stabil nach Cart-Reihenfolge
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
}
