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
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {debounceTime, distinctUntilChanged, Subject} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";

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
    MatSuffix,
    MatPaginator
  ],
  standalone: true,
  styleUrls: ['./events-list.component.scss']
})
export class EventsListComponent implements OnInit {
  events: EventDto[] = [];
  eventImages: Map<number, SafeUrl> = new Map();
  halls: Hall[] = [];
  hallsWithVenues: { hall: Hall; venueName?: string }[] = [];

  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalEvents: number = 0;
  totalPages: number = 0;
  private isSearchActive: boolean = false;

  searchTitle: string = '';
  searchArtist: string = '';
  searchLocation: string = '';
  selectedEventType: EventTypeDto | null = null;
  selectedStartDate: Date | null = null;
  selectedDuration: number | null = null;

  private searchSubject = new Subject<void>();
  private dateSearchSubject = new Subject<void>();

  eventTypes = Object.values(EventTypeDto);
  isLoading: boolean = false;

  // Admin-Eigenschaften
  currentEvent: EventDto | null = null;
  selectedFile: File | null = null;
  isEditMode: boolean = false;
  performances: PerformanceDto[] = [];
  newPerformance: Partial<PerformanceDto> = {};
  newPerformancePriceEuros: number | null = null;

  constructor(
    private eventsService: EventsService,
    private sanitizer: DomSanitizer,
    private performanceService: PerformancesService,
    private modalService: NgbModal,
    private authService: AuthService,
    private hallsService: HallsService,
    private venuesService: VenuesService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.loadEvents();
    this.loadHalls();

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.performSearch();
    });

    this.dateSearchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.performSearch();
    });
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  loadEvents(): void {
    this.isLoading = true;
    this.eventsService.getEvents(this.currentPage, this.pageSize).subscribe({
      next: (pagedResult) => {
        this.events = pagedResult.content;
        this.totalEvents = pagedResult.totalElements;
        this.totalPages = pagedResult.totalPages;
        this.loadImagesForEvents(this.events);
        this.isLoading = false;
      },
      error: (error) => {
        this.showErrorSnackbar('Fehler beim Laden der Events');
        this.isLoading = false;
      }
    });
  }


  performSearch(): void {
    this.isSearchActive = true;
    this.currentPage = 0;  // Immer auf Seite 0 zurücksetzen
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
    }, this.currentPage, this.pageSize).subscribe({
      next: (pagedResult) => {
        this.events = pagedResult.content;
        this.totalEvents = pagedResult.totalElements;
        this.totalPages = pagedResult.totalPages;
        this.loadImagesForEvents(this.events);
        this.isLoading = false;
      },
      error: (error) => {
        this.showErrorSnackbar('Fehler bei der Suche');
        this.isLoading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;

    // Einfache Logik: Wenn isSearchActive, dann performSearch, sonst loadEvents
    if (this.isSearchActive) {
      this.performSearch();
    } else {
      this.loadEvents();
    }
  }

  onDateChange(): void {
    this.dateSearchSubject.next();
  }

  resetFilters(): void {
    this.searchTitle = '';
    this.searchArtist = '';
    this.searchLocation = '';
    this.selectedEventType = null;
    this.selectedStartDate = null;
    this.selectedDuration = null;
    this.currentPage = 0;
    this.isSearchActive = false;
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
    this.newPerformancePriceEuros = null;
    this.modalService.open(eventAddModal, {size: 'lg'});
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  performanceErrors: { [key: string]: string } = {};

  calculateDurationFromPerformances(): number | null {
    if (this.performances.length === 0) {
      return null;
    }

    const firstPerf = this.performances[0];
    const startTime = new Date(firstPerf.startTime);
    const endTime = new Date(firstPerf.endTime);
    const durationMs = endTime.getTime() - startTime.getTime();
    const durationMinutes = Math.round(durationMs / (1000 * 60));

    return durationMinutes > 0 ? durationMinutes : null;
  }

  addPerformanceToList(): void {
    // Validiere Halle, Start- und Endzeit
    const baseValidation = this.eventsService.validatePerformanceBase(this.newPerformance);
    if (!baseValidation.valid) {
      this.performanceErrors = baseValidation.fieldErrors;
      return;
    }

    // Validiere Preis separat
    if (this.newPerformancePriceEuros === null || this.newPerformancePriceEuros === undefined || this.newPerformancePriceEuros < 0) {
      this.performanceErrors['basePrice'] = 'Basispreis darf nicht negativ sein';
      return;
    }

    // Berechne Dauer aus der ersten Performance
    if (this.performances.length === 0 && this.newPerformance.startTime && this.newPerformance.endTime) {
      const startTime = new Date(this.newPerformance.startTime);
      const endTime = new Date(this.newPerformance.endTime);
      const durationMs = endTime.getTime() - startTime.getTime();
      const durationMinutes = Math.round(durationMs / (1000 * 60));

      if (this.currentEvent) {
        this.currentEvent.durationMinutes = durationMinutes;
      }
    }

    // Validiere, dass alle Performances die gleiche Dauer haben
    if (this.performances.length > 0 && this.newPerformance.startTime && this.newPerformance.endTime) {
      const startTime = new Date(this.newPerformance.startTime);
      const endTime = new Date(this.newPerformance.endTime);
      const durationMs = endTime.getTime() - startTime.getTime();
      const durationMinutes = Math.round(durationMs / (1000 * 60));

      if (this.currentEvent && durationMinutes !== this.currentEvent.durationMinutes) {
        this.performanceErrors['duration'] = 'Alle Aufführungen müssen die gleiche Dauer haben';
        return;
      }
    }

    // Konvertiere Preis von Euro zu Cents
    const performanceToAdd: PerformanceDto = {
      ...this.newPerformance as PerformanceDto,
      basePriceCents: Math.round(this.newPerformancePriceEuros * 100),
      id: Date.now()
    };

    this.performances.push(performanceToAdd);
    this.newPerformance = {};
    this.newPerformancePriceEuros = null;
    this.performanceErrors = {};
  }





  removePerformance(index: number): void {
    this.performances.splice(index, 1);
    // Berechne Dauer neu, wenn noch Performances vorhanden sind
    if (this.performances.length > 0) {
      const duration = this.calculateDurationFromPerformances();
      if (this.currentEvent && duration) {
        this.currentEvent.durationMinutes = duration;
      }
    } else {
      if (this.currentEvent) {
        this.currentEvent.durationMinutes = 0;
      }
    }
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
      this.showErrorSnackbar('Event-Daten fehlen');
      return;
    }

    if (this.performances.length === 0) {
      this.showErrorSnackbar('Mindestens eine Aufführung ist erforderlich');
      return;
    }

    const validation = this.eventsService.validateEvent(this.currentEvent);
    if (!validation.valid) {
      this.showErrorSnackbar(validation.errors.join(', '));
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
        this.showErrorSnackbar(error.error?.error || 'Fehler beim Erstellen des Events');
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

  private showErrorSnackbar(message: string): void {
    this.snackBar.open(message, 'Schließen', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['error-snackbar'],
    });


  }

}
