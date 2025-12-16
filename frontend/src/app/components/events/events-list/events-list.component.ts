import {Component, OnInit, TemplateRef} from '@angular/core';
import {EventsService} from '../../../services/events.service';
import {EventDto, EventTypeDto} from '../../../dtos/event';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatSelect} from '@angular/material/select';
import {MatOption} from '@angular/material/core';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {FormsModule, NgForm} from '@angular/forms';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatIconModule} from "@angular/material/icon";
import {PerformanceDto} from "../../../dtos/performanceDto";
import {PerformancesService} from "../../../services/performances.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AuthService} from "../../../services/auth.service";
import {Hall} from "../../../dtos/hall";
import {HallsService} from "../../../services/halls.service";
import {VenuesService} from "../../../services/venues.service";

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
  halls: Hall[] = [];
  hallsWithVenues: { hall: Hall; venueName?: string }[] = [];


  searchTitle: string = '';
  searchArtist: string = '';
  searchLocation: string = '';
  selectedEventType: EventTypeDto | null = null;
  selectedStartDate: Date | null = null;
  selectedDuration: number | null = null;

  eventTypes = Object.values(EventTypeDto);
  isLoading: boolean = false;

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
    private authService: AuthService,
    private hallsService: HallsService,
    private venuesService: VenuesService
  ) {
  }

  ngOnInit(): void {
    this.loadEvents();
    this.loadHalls();
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
    this.modalService.open(eventAddModal, {size: 'lg'});
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  performanceErrors: { [key: string]: string } = {};

  addPerformanceToList(): void {
    const validation = this.eventsService.validatePerformance(this.newPerformance);
    if (!validation.valid) {
      this.performanceErrors = validation.fieldErrors;
      return;
    }

    this.performances.push({
      ...this.newPerformance,
      id: Date.now()
    } as PerformanceDto);

    this.newPerformance = {};
    this.performanceErrors = {};
    this.error = false;
  }


  removePerformance(index: number): void {
    this.performances.splice(index, 1);
  }

  private loadHalls(): void {
    this.hallsService.getAll().subscribe({
      next: (halls) => {
        this.hallsWithVenues = halls.map(hall => ({ hall }));
        this.loadVenuesForHalls();
      },
      error: (error) => {
        console.error('Fehler beim Laden der Hallen:', error);
      }
    });
  }

  private loadVenuesForHalls(): void {
    this.hallsWithVenues.forEach(item => {
      if (item.hall.venueId) {
        this.venuesService.getById(item.hall.venueId).subscribe({
          next: (venue) => {
            item.venueName = venue.name;
          },
          error: () => {
            console.warn(`Venue für Hall ${item.hall.id} konnte nicht geladen werden`);
          }
        });
      }
    });
  }

  saveEvent(modal: any, eventForm: NgForm): void {
    if (!eventForm.valid) {
      eventForm.form.markAllAsTouched();
      return;
    }

    if (!this.currentEvent) {
      this.error = true;
      this.errorMessage = 'Event-Daten fehlen';
      return;
    }

    // Service-Validierung durchführen
    const validation = this.eventsService.validateEvent(this.currentEvent);
    if (!validation.valid) {
      this.error = true;
      this.errorMessage = validation.errors.join(', ');
      return;
    }

    this.isLoading = true;
    this.eventsService.createEvent(this.currentEvent, this.selectedFile || undefined).subscribe({
      next: (createdEvent) => {
        this.createPerformances(createdEvent.id);
        this.loadEvents();
        modal.dismiss();
        this.isLoading = false;
      },
      error: (error) => {
        this.error = true;
        this.errorMessage = error.error?.error || 'Fehler beim Erstellen des Events';
        this.isLoading = false;
      }
    });
  }

  eventTypeLabels: { [key: string]: string } = {
    'CONCERT': 'KONZERT',
    'FESTIVAL': 'FESTIVAL',
    'MUSICAL': 'MUSICAL'
  };

  getEventTypeLabel(type: EventTypeDto | string): string {
    return this.eventTypeLabels[type] || type;
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
