import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ticket } from '../../dtos/ticket';
import { TicketsService } from '../../services/tickets.service';
import { TicketCardComponent } from '../tickets/ticket-card/ticket-card.component';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [
    CommonModule,
    TicketCardComponent
  ],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.scss',
})
export class TicketsComponent {

  loading = false;
  tickets: Ticket[] = [];

  constructor(private ticketsService: TicketsService) {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.ticketsService.getTickets().subscribe({
      next: (tickets: Ticket[]) => {
        this.tickets = tickets;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
