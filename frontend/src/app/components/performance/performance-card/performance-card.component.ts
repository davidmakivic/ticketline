import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { PerformanceDto } from '../../../dtos/performanceDto';
import { Hall } from '../../../dtos/hall';
import { Venue } from '../../../dtos/venue';
import { HallsService } from '../../../services/halls.service';
import { VenuesService } from '../../../services/venues.service';
import { EventsService } from '../../../services/events.service';
import {Subject, takeUntil} from "rxjs";

@Component({
  selector: 'app-performance-card',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule
  ],
  templateUrl: './performance-card.component.html',
  styleUrl: './performance-card.component.scss',
})
export class PerformanceCardComponent implements OnInit, OnChanges {

  @Input({ required: true })
  performance!: PerformanceDto;

  @Input()
  showEventImage: boolean = false;

  hall: Hall | null = null;
  venue: Venue | null = null;
  eventImage: SafeUrl | null = null;
  loadingLocation = false;
  @Input() showEventTitle!: boolean;

  private destroy$ = new Subject<void>();

  constructor(
    private hallsService: HallsService,
    private venuesService: VenuesService,
    private eventsService: EventsService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadLocationData();
    if (this.showEventImage) {
      this.loadEventImage();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['performance'] && !changes['performance'].firstChange) {
      this.loadLocationData();
      if (this.showEventImage) {
        this.loadEventImage();
      }
    }
  }

  ngOnDestroy(): void {
    if (this.eventImage) {
      const url = this.eventImage.toString();
      if (url.startsWith('blob:')) {
        URL.revokeObjectURL(url);
      }
    }
    this.destroy$.next();
    this.destroy$.complete();
  }

  get basePriceEuro(): number {
    return this.performance.basePriceCents / 100;
  }

  get fullAddress(): string | null {
    if (!this.venue) {
      return null;
    }

    const parts = [
      this.venue.street,
      [this.venue.postalCode, this.venue.city].filter(Boolean).join(' ')
    ].filter(Boolean);

    return parts.join(', ');
  }

  private loadLocationData(): void {
    if (!this.performance || !this.performance.hallId) {
      return;
    }

    this.loadingLocation = true;

    this.hallsService.getById(this.performance.hallId).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: hall => {
        this.hall = hall;

        if (!hall.venueId) {
          this.loadingLocation = false;
          return;
        }

        this.venuesService.getById(hall.venueId).pipe(
          takeUntil(this.destroy$)
        ).subscribe({
          next: venue => {
            this.venue = venue;
            this.loadingLocation = false;
          },
          error: () => {
            this.loadingLocation = false;
          }
        });
      },
      error: () => {
        this.loadingLocation = false;
      }
    });
  }

  private loadEventImage(): void {
    if (!this.performance || !this.performance.eventId) {
      return;
    }

    this.eventsService.getEventImage(this.performance.eventId).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        this.eventImage = this.sanitizer.bypassSecurityTrustUrl(url);
      },
      error: () => {
        // Bild konnte nicht geladen werden
      }
    });
  }
}
