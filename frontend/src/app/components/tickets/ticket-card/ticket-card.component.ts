import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

import { Ticket, TicketStatus } from '../../../dtos/ticket';

@Component({
  selector: 'app-ticket-card',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './ticket-card.component.html',
  styleUrl: './ticket-card.component.scss',
})
export class TicketCardComponent {

  @Input({ required: true })
  ticket!: Ticket;

  priceEuro(): number {
    return this.ticket.priceFinalCents / 100;
  }

  statusLabel(): string {
    switch (this.ticket.status) {
      case TicketStatus.AVAILABLE: return 'Verfügbar';
      case TicketStatus.RESERVED: return 'Reserviert';
      case TicketStatus.PURCHASED: return 'Gekauft';
      default: return this.ticket.status;
    }
  }

  statusClass(): string {
    switch (this.ticket.status) {
      case TicketStatus.AVAILABLE: return 'status-available';
      case TicketStatus.RESERVED: return 'status-reserved';
      case TicketStatus.PURCHASED: return 'status-purchased';
      default: return 'status-unknown';
    }
  }
}
