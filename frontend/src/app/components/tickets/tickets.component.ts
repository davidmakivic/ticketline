import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ticket, TicketStatus } from '../../dtos/ticket';
import { TicketCartItemComponent } from '../tickets/ticket-cart-item/ticket-cart-item.component'; // <- anpassen!

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [
    CommonModule,
    TicketCartItemComponent, // <- wichtig
  ],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.scss',
})
export class TicketsComponent {

  // zum schnellen Preview: nur 1 Ticket
  tickets: Ticket[] = [{
    id: 1,
    performanceId: 10,
    seatId: 55,
    priceFinalCents: 2500,
    status: TicketStatus.RESERVED,
    version: 0
  }];

  onRemove(ticket: Ticket) {
    console.log('remove clicked', ticket);
    // fürs Preview reicht das
  }
}
