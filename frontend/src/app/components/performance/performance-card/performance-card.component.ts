import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

import { Performance } from '../../../dtos/performance';
import { Hall } from '../../../dtos/hall';
import { Venue } from '../../../dtos/venue';
import { HallsService } from '../../../services/halls.service';
import { VenuesService } from '../../../services/venues.service';

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
  performance!: Performance;

  hall: Hall | null = null;
  venue: Venue | null = null;

  loadingLocation = false;

  constructor(
    private hallsService: HallsService,
    private venuesService: VenuesService
  ) {}

  ngOnInit(): void {
    this.loadLocationData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['performance'] && !changes['performance'].firstChange) {
      this.loadLocationData();
    }
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

    this.hallsService.getById(this.performance.hallId).subscribe({
      next: hall => {
        this.hall = hall;

        if (!hall.venueId) {
          this.loadingLocation = false;
          return;
        }

        this.venuesService.getById(hall.venueId).subscribe({
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
}
