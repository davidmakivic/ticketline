import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

import { PerformanceDto } from '../../../dtos/performanceDto';
import { PerformancesService } from '../../../services/performances.service';
import { PerformanceCardComponent } from '../performance-card/performance-card.component';
import { EventTypeDto, PagedResult } from '../../../dtos/event';

@Component({
  selector: 'app-performances-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    FormsModule,
    PerformanceCardComponent
  ],
  templateUrl: './performances-list.component.html',
  styleUrl: './performances-list.component.scss',
})
export class PerformancesListComponent implements OnInit {
  performances: PerformanceDto[] = [];
  loading = false;
  pageSize = 10;
  pageIndex = 0;
  totalElements = 0;
  totalPages = 0;

  // Filter
  searchTitle: string = '';
  searchLocation: string = '';
  selectedEventType: EventTypeDto | null = null;
  selectedStartDate: Date | null = null;
  eventTypes = Object.values(EventTypeDto);

  searchArtist: string = '';
  selectedDuration: number | null = null;
  durationOptions = [30, 60, 90, 120, 150, 180];

  private isSearchActive = false;
  protected searchSubject = new Subject<void>();

  constructor(private performancesService: PerformancesService) {}

  ngOnInit(): void {
    this.loadPerformances();

    this.searchSubject.pipe(
      debounceTime(500)
    ).subscribe(() => {
      this.performSearch();
    });
  }

  private loadPerformances(): void {
    this.loading = true;
    this.performancesService.getAll(this.pageIndex, this.pageSize).subscribe({
      next: (pagedResult: PagedResult<PerformanceDto>) => {
        this.performances = pagedResult.content;
        this.totalElements = pagedResult.totalElements;
        this.totalPages = pagedResult.totalPages;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearchInputChange(): void {
    this.isSearchActive = this.hasActiveFilters();
    this.pageIndex = 0;
    this.searchSubject.next();
  }

  onEventTypeChange(): void {
    this.isSearchActive = this.hasActiveFilters();
    this.pageIndex = 0;
    this.performSearch();
  }

  onStartDateChange(): void {
    this.isSearchActive = this.hasActiveFilters();
    this.pageIndex = 0;
    this.performSearch();
  }

  private hasActiveFilters(): boolean {
    return !!(
      this.searchTitle?.trim() ||
      this.searchLocation?.trim() ||
      this.selectedEventType ||
      this.selectedStartDate
    );
  }

  performSearch(): void {
    this.loading = true;
    let startDateFormatted: Date | undefined = undefined;

    if (this.selectedStartDate) {
      const normalizedDate = new Date(this.selectedStartDate);
      normalizedDate.setHours(12, 0, 0, 0);
      startDateFormatted = normalizedDate;
    }

    this.performancesService.searchAdvanced({
      title: this.searchTitle?.trim() || undefined,
      artist: this.searchArtist?.trim() || undefined,
      location: this.searchLocation?.trim() || undefined,
      eventType: this.selectedEventType || undefined,
      startDate: startDateFormatted,
      durationMinutes: this.selectedDuration || undefined
    }, this.pageIndex, this.pageSize).subscribe({
      next: (pagedResult: PagedResult<PerformanceDto>) => {
        this.performances = pagedResult.content;
        this.totalElements = pagedResult.totalElements;
        this.totalPages = pagedResult.totalPages;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }


  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;

    if (this.isSearchActive) {
      this.performSearch();
    } else {
      this.loadPerformances();
    }
  }

// resetFilters() anpassen
  resetFilters(): void {
    this.searchTitle = '';
    this.searchArtist = '';
    this.searchLocation = '';
    this.selectedEventType = null;
    this.selectedStartDate = null;
    this.selectedDuration = null;
    this.pageIndex = 0;
    this.isSearchActive = false;
    this.loadPerformances();
  }

  getEventTypeLabel(type: EventTypeDto): string {
    const labels: { [key: string]: string } = {
      'CONCERT': 'KONZERT',
      'FESTIVAL': 'FESTIVAL',
      'MUSICAL': 'MUSICAL'
    };
    return labels[type] || type;
  }
}

