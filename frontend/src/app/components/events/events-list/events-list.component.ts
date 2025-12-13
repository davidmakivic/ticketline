import { Component, OnInit } from '@angular/core';
import { EventsService } from '../../../services/events.service';
import { EventDto, EventTypeDto } from '../../../dtos/event';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatOption } from '@angular/material/core';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import { MatCard, MatCardContent, MatCardHeader, MatCardTitle } from '@angular/material/card';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import {MatIcon, MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-events-list',
  templateUrl: './events-list.component.html',
  imports: [
    CommonModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatSelect,
    MatOption,
    MatDatepickerInput,
    FormsModule,
    MatCard,
    MatCardTitle,
    MatCardHeader,
    MatProgressSpinner,
    MatDatepickerToggle,
    MatDatepicker,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardContent,
    MatButton,
    MatIconModule,
    RouterLink,
    MatSuffix
  ],
  styleUrls: ['./events-list.component.scss']
})
export class EventsListComponent implements OnInit {
  events: EventDto[] = [];
  eventImages: Map<number, SafeUrl> = new Map();

  searchTitle: string = '';
  searchArtist: string = '';
  searchLocation: string = '';
  selectedEventType: EventTypeDto | null = null;
  selectedStartDate: Date | null = null;
  selectedDuration: number | null = null;

  eventTypes = Object.values(EventTypeDto);
  isLoading: boolean = false;

  constructor(
    private eventsService: EventsService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.isLoading = true;
    this.eventsService.getEvents().subscribe({
      next: (data) => {
        this.events = data;
        this.loadImagesForEvents(data);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Fehler beim Laden der Events:', error);
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.isLoading = true;
    let startDateFormatted: Date | undefined = undefined;

    if (this.selectedStartDate) {
      // Stelle sicher, dass es ein Date-Objekt ist
      startDateFormatted = this.selectedStartDate instanceof Date
        ? this.selectedStartDate
        : new Date(this.selectedStartDate);
    }
    this.eventsService.searchAdvanced({
      title: this.searchTitle || undefined,
      artist: this.searchArtist || undefined,
      location: this.searchLocation || undefined,
      eventType: this.selectedEventType || undefined,
      startDate: startDateFormatted,
      durationMinutes: this.selectedDuration || undefined
    }).subscribe({
      next: (events) => {
        this.events = events;
        this.loadImagesForEvents(events);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Fehler bei der Suche:', error);
        this.isLoading = false;
      }
    });
  }

  resetFilters(): void {
    this.searchTitle = '';
    this.searchArtist = '';
    this.searchLocation = '';
    this.selectedEventType = null;
    this.selectedStartDate = null;
    this.selectedDuration = null;
    this.eventImages.clear();
    this.loadEvents();
  }

  getEventImage(eventId: number): SafeUrl | undefined {
    return this.eventImages.get(eventId);
  }

  loadImagesForEvents(events: EventDto[]): void {
    events.forEach(event => {
      if (event.id && !this.eventImages.has(event.id)) {
        this.eventsService.getEventImage(event.id).subscribe({
          next: (blob) => {
            const url = window.URL.createObjectURL(blob);
            const safeUrl = this.sanitizer.bypassSecurityTrustUrl(url);
            this.eventImages.set(event.id!, safeUrl);
          },
          error: (error) => {
            console.warn(`Bild für Event ${event.id} konnte nicht geladen werden:`, error);
          }
        });
      }
    });
  }
}
