import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { forkJoin, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

import { Ticket } from '../../../dtos/ticket';
import { PerformanceDto } from '../../../dtos/performanceDto';
import { EventDto } from '../../../dtos/event';
import { Hall } from '../../../dtos/hall';
import { Venue } from '../../../dtos/venue';
import { Seat } from '../../../dtos/seat';

import { PerformancesService } from '../../../services/performances.service';
import { EventsService } from '../../../services/events.service';
import { HallsService } from '../../../services/halls.service';
import { VenuesService } from '../../../services/venues.service';
import { SeatsService } from '../../../services/seats.service';

@Component({
  selector: 'app-ticket-cart-item',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './ticket-cart-item.component.html',
  styleUrl: './ticket-cart-item.component.scss',
})
export class TicketCartItemComponent implements OnChanges {

  @Input({ required: true }) ticket!: Ticket;

  @Output() remove = new EventEmitter<Ticket>();

  loading = false;

  eventTitle: string | null = null;
  eventImageUrl: SafeUrl | null = null;

  performance: PerformanceDto | null = null;
  hall: Hall | null = null;
  venue: Venue | null = null;

  fullAddress: string | null = null;

  sectorName: string | null = null;
  rowNumber: number | null = null;
  seatNumber: number | null = null;

  priceEuro: number | null = null;

  constructor(
    private performancesService: PerformancesService,
    private eventsService: EventsService,
    private hallsService: HallsService,
    private venuesService: VenuesService,
    private seatsService: SeatsService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ticket']) {
      this.loadAll();
    }
  }

  onRemoveClick(): void {
    this.remove.emit(this.ticket);
  }

  private loadAll(): void {
    if (!this.ticket) return;

    this.loading = true;

    this.priceEuro = this.ticket.priceFinalCents / 100;

    this.performancesService.getById(this.ticket.performanceId).pipe(
      switchMap(perf => {
        this.performance = perf;

        return forkJoin({
          event: this.eventsService.getEventById(perf.eventId).pipe(
            catchError(() => of(null))
          ),
          hall: this.hallsService.getById(perf.hallId).pipe(
            catchError(() => of(null))
          )
        });
      }),
      switchMap(({ event, hall }) => {
        this.hall = hall;

        if (event) {
          this.eventTitle = event.title;
          this.eventsService.getEventImage(event.id).pipe(
            catchError(() => of(null))
          ).subscribe(blob => {
            if (blob) {
              const url = URL.createObjectURL(blob);
              this.eventImageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
            } else {
              this.eventImageUrl = null;
            }

          });
        } else {
          this.eventTitle = null;
          this.eventImageUrl = null;
        }

        if (hall?.venueId) {
          return this.venuesService.getById(hall.venueId).pipe(
            catchError(() => of(null)),
            switchMap(venue => {
              this.venue = venue;
              this.fullAddress = venue
                ? [venue.street, [venue.postalCode, venue.city].filter(Boolean).join(' ')].filter(Boolean).join(', ')
                : null;

              return this.loadSeatAndSector(hall);
            })
          );
        }

        this.venue = null;
        this.fullAddress = null;
        return this.loadSeatAndSector(hall);
      }),
      catchError(() => of(null))
    ).subscribe({
      next: () => {
 this.loading = false;
},
      error: () => {
 this.loading = false;
}
    });
  }

  private loadSeatAndSector(hall: Hall | null) {
    if (!this.ticket.seatId) {
      this.sectorName = null;
      this.rowNumber = null;
      this.seatNumber = null;
      return of(null);
    }

    return this.seatsService.getById(this.ticket.seatId).pipe(
      catchError(() => of(null as Seat | null)),
      switchMap(seat => {
        if (!seat) {
          this.sectorName = null;
          this.rowNumber = null;
          this.seatNumber = null;
          return of(null);
        }

        this.rowNumber = seat.rowNumber;
        this.seatNumber = seat.seatNumber;

        const sectorEntry = hall?.sectorIndex?.find(s => s.id === seat.sectorId);
        this.sectorName = sectorEntry?.name ?? null;

        return of(null);
      })
    );
  }
}
