import {Component, OnInit, TemplateRef} from '@angular/core';
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
import {FormsModule, NgForm} from '@angular/forms';
import { MatCard, MatCardContent, MatCardHeader, MatCardTitle } from '@angular/material/card';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import {MatIconModule} from "@angular/material/icon";
import {PerformanceDto} from "../../../dtos/performanceDto";
import {PerformancesService} from "../../../services/performances.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AuthService} from "../../../services/auth.service";

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
  standalone: true,
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
  performanceSubmitted: boolean = false;

  // Admin-Eigenschaften
  currentEvent: EventDto | null = null;
  selectedFile: File | null = null;
  isEditMode: boolean = false;
  performances: PerformanceDto[] = [];
  newPerformance: Partial<PerformanceDto> = {};
  error: boolean = false;
  errorMessage: string = '';

  constructor(
    private eventsService: EventsService,
    private sanitizer: DomSanitizer,
    private performanceService: PerformancesService,
    private modalService: NgbModal,
    private authService: AuthService

  ) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
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

  openAddEventModal(eventAddModal: TemplateRef<any>): void {
    this.isEditMode = false;
    this.currentEvent = {
      title: '',
      description: '',
      category: EventTypeDto.CONCERT,
      durationMinutes: 0,
      artists: [],
      performances: []
    } as EventDto;
    this.selectedFile = null;
    this.performances = [];
    this.newPerformance = {};
    this.error = false;
    this.modalService.open(eventAddModal, { size: 'lg' });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  addPerformanceToList(): void {
    this.performanceSubmitted = true; // für die Fehlermeldungen

    const p = this.newPerformance;

    // Validierung
    if (
      !p.startTime ||
      !p.endTime ||
      !p.hallId ||
      p.hallId <= 0 ||
      p.basePriceCents === undefined ||
      p.basePriceCents < 0 ||
      new Date(p.endTime) <= new Date(p.startTime)
    ) {
      return; // ungültige Eingaben -> nicht hinzufügen
    }

    this.performances.push({
      ...p,
      id: Date.now() // temporäre ID für Frontend
    } as PerformanceDto);

    this.newPerformance = {};
    this.performanceSubmitted = false; // reset für nächste Eingabe
  }

  removePerformance(index: number): void {
    this.performances.splice(index, 1);
  }

  saveEvent(eventAddModal: any): void {
    if (!this.currentEvent || !this.currentEvent.title || !this.currentEvent.description) {
      this.error = true;
      this.errorMessage = 'Titel und Beschreibung sind erforderlich!';
      return;
    }

    this.isLoading = true;
    this.eventsService.createEvent(this.currentEvent, this.selectedFile || undefined).subscribe({
      next: (createdEvent) => {
        this.createPerformances(createdEvent.id);
        this.loadEvents();
        eventAddModal.dismiss();
        this.isLoading = false;
      },
      error: (error) => {
        this.error = true;
        this.errorMessage = error.error?.error || 'Fehler beim Erstellen des Events';
        this.isLoading = false;
      }
    });
  }

  private createPerformances(eventId: number): void {
    this.performances.forEach(perf => {
      const perfDto = {
        eventId: eventId,
        hallId: perf.hallId,
        startTime: perf.startTime,
        endTime: perf.endTime,
        basePriceCents: perf.basePriceCents
      };
      this.performanceService.create(perfDto).subscribe({
        error: (error) => console.error('Fehler beim Erstellen der Performance:', error)
      });
    });
  }

  vanishError(): void {
    this.error = false;
  }

}
