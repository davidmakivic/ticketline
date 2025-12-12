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
export class PerformanceCardComponent {

  @Input({ required: true })
  performance!: Performance;

  get basePriceEuro(): number {
    return this.performance.basePriceCents / 100;
  }
}
