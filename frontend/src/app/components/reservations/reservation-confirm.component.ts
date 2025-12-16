import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { TicketsService } from '../../services/tickets.service';
import { ReservationsService } from '../../services/reservations.service';
import { PendingReservationService } from '../../services/pending-reservation.service';
import { AuthService } from '../../services/auth.service';

import { Ticket, TicketStatus } from '../../dtos/ticket';
import { ReservationDto } from '../../dtos/reservation.dto';
import { TicketCardComponent } from '../tickets/ticket-card/ticket-card.component';


@Component({
  selector: 'app-reservation-confirm',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    TicketCardComponent
   ],
  templateUrl: './reservation-confirm.component.html',
  styleUrls: ['./reservation-confirm.component.scss']
})
export class ReservationConfirmComponent {

  loading = true;
  saving = false;
  error: string | null = null;

  ticketIds: number[] = [];
  tickets: Ticket[] = [];

  constructor(
    private pending: PendingReservationService,
    private auth: AuthService,
    private ticketsService: TicketsService,
    private reservations: ReservationsService,
    private router: Router
  ) {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { redirect: '/reserve/confirm' } });
      return;
    }

    this.ticketIds = this.pending.getTicketIds();
    if (this.ticketIds.length === 0) {
      this.loading = false;
      this.error = 'Keine Tickets zur Reservierung ausgewählt.';
      return;
    }

    this.loadTickets();
  }

  private loadTickets() {
    this.loading = true;
    this.error = null;

    forkJoin(
      this.ticketIds.map(id =>
        this.ticketsService.getTicketById(id).pipe(catchError(() => of(null)))
      )
    ).pipe(
      map(list => list.filter((t): t is Ticket => t !== null))
    ).subscribe({
      next: tickets => {
        this.tickets = tickets;
        this.loading = false;
      },
      error: () => {
        this.error = 'Tickets konnten nicht geladen werden.';
        this.loading = false;
      }
    });
  }

  confirmReservation() {
    if (this.saving) return;
    if (this.ticketIds.length === 0) return;

    this.saving = true;
    this.error = null;

    this.reservations.create({ ticketIds: this.ticketIds }).subscribe({
      next: (res: ReservationDto) => {
        this.pending.clear();


        this.router.navigate(['/reserve/success'], {
          state: { reservation: res }
        });
      },
      error: (e) => {
        console.error(e);
        this.error = 'Reservierung fehlgeschlagen (Tickets evtl. nicht mehr verfügbar).';
        this.saving = false;
      }
    });
  }

  cancel() {
    this.pending.clear();
    this.router.navigate(['/performances']);
  }

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }

  get totalCents(): number {
    return (this.tickets ?? []).reduce((sum, t) => sum + (t.priceFinalCents ?? 0), 0);
  }

}
