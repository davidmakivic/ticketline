import {Injectable} from "@angular/core";
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {EventAutocompleteDto, EventDto, EventTop10Dto, EventTypeDto, PagedResult} from "../dtos/event";
import {PerformanceDto} from "../dtos/performanceDto";

@Injectable({providedIn: "root"})
export class EventsService {
  private eventsBaseUri: string = this.globals.backendUri + "/events";

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {}

  getEvents(page: number = 0, size: number = 10): Observable<PagedResult<EventDto>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.httpClient.get<PagedResult<EventDto>>(this.eventsBaseUri, { params });
  }

  searchAdvanced(filters: {
    title?: string;
    artist?: string;
    location?: string;
    eventType?: string;
    startDate?: Date;
    durationMinutes?: number;
  }, page: number = 0, size: number = 10): Observable<PagedResult<EventDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters.title) params = params.set('title', filters.title);
    if (filters.artist) params = params.set('artist', filters.artist);
    if (filters.location) params = params.set('location', filters.location);
    if (filters.eventType) params = params.set('eventType', filters.eventType);
    if (filters.startDate) params = params.set('startDate', this.formatDate(filters.startDate));
    if (filters.durationMinutes) params = params.set('durationMinutes', filters.durationMinutes.toString());

    return this.httpClient.get<PagedResult<EventDto>>(`${this.eventsBaseUri}/query`, { params });
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getEventAutocompleteByTitle(title:string, limit:number): Observable<EventAutocompleteDto[]>{
    const params = new HttpParams()
      .set("title", title)
      .set("limit", limit);

    return this.httpClient.get<EventAutocompleteDto[]>(`${this.eventsBaseUri}/autocomplete`, {params});
  }

  getTop10Events(eventType: EventTypeDto | null): Observable<EventTop10Dto[]> {
    if (eventType === null) {
      return this.httpClient.get<EventTop10Dto[]>(`${this.eventsBaseUri}/top10`);
    }
    const params = new HttpParams()
      .set("eventType", eventType);
    return this.httpClient.get<EventTop10Dto[]>(`${this.eventsBaseUri}/top10`, {params});

  }

  getEventById(id: number): Observable<EventDto> {
    return this.httpClient.get<EventDto>(`${this.eventsBaseUri}/${id}`);
  }

  getEventImage(id: number): Observable<Blob> {
    return this.httpClient.get(`${this.eventsBaseUri}/${id}/image`, { responseType: 'blob' });
  }

  createEvent(event: EventDto, image?: File): Observable<EventDto> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Events erstellen');
    }
    const formData = new FormData();
    formData.append('title', event.title);
    formData.append('description', event.description);
    formData.append('category', event.category);
    formData.append('durationMinutes', event.durationMinutes.toString());
    if (image) {
      formData.append('image', image);
    }
    return this.httpClient.post<EventDto>(this.eventsBaseUri, formData);
  }

  validateEvent(event: EventDto): { valid: boolean; errors: string[] } {
    const errors: string[] = [];

    // Titel validieren
    if (!event.title || event.title.trim().length === 0) {
      errors.push('Titel ist erforderlich');
    } else if (event.title.length < 3) {
      errors.push('Titel muss mindestens 3 Zeichen lang sein');
    } else if (event.title.length > 100) {
      errors.push('Titel darf maximal 100 Zeichen lang sein');
    }

    // Beschreibung validieren
    if (!event.description || event.description.trim().length === 0) {
      errors.push('Beschreibung ist erforderlich');
    } else if (event.description.length < 10) {
      errors.push('Beschreibung muss mindestens 10 Zeichen lang sein');
    } else if (event.description.length > 1000) {
      errors.push('Beschreibung darf maximal 1000 Zeichen lang sein');
    }

    // Kategorie validieren
    if (!event.category || !Object.values(EventTypeDto).includes(event.category)) {
      errors.push('Kategorie ist erforderlich und muss gültig sein');
    }

    // Dauer validieren
    if (!event.durationMinutes || event.durationMinutes < 1) {
      errors.push('Dauer muss mindestens 1 Minute sein');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  validatePerformance(performance: Partial<PerformanceDto>): { valid: boolean; fieldErrors: { [key: string]: string } } {
    const fieldErrors: { [key: string]: string } = {};

    if (!performance.hallId || performance.hallId <= 0) {
      fieldErrors['hallId'] = 'Halle ID muss größer als 0 sein';
    }

    if (!performance.startTime) {
      fieldErrors['startTime'] = 'Startzeit ist erforderlich';
    }

    if (!performance.endTime) {
      fieldErrors['endTime'] = 'Endzeit ist erforderlich';
    }

    if (performance.startTime && performance.endTime) {
      if (new Date(performance.endTime) <= new Date(performance.startTime)) {
        fieldErrors['endTime'] = 'Endzeit muss nach Startzeit liegen';
      }
    }

    if (performance.basePriceCents === undefined || performance.basePriceCents < 0) {
      fieldErrors['basePrice'] = 'Basispreis darf nicht negativ sein';
    }

    return {
      valid: Object.keys(fieldErrors).length === 0,
      fieldErrors
    };
  }

  validatePerformanceBase(performance: Partial<PerformanceDto>): { valid: boolean; fieldErrors: { [key: string]: string } } {
    const fieldErrors: { [key: string]: string } = {};

    if (!performance.hallId || performance.hallId <= 0) {
      fieldErrors['hallId'] = 'Halle ID muss größer als 0 sein';
    }

    if (!performance.startTime) {
      fieldErrors['startTime'] = 'Startzeit ist erforderlich';
    }

    if (!performance.endTime) {
      fieldErrors['endTime'] = 'Endzeit ist erforderlich';
    }

    if (performance.startTime && performance.endTime) {
      if (new Date(performance.endTime) <= new Date(performance.startTime)) {
        fieldErrors['endTime'] = 'Endzeit muss nach Startzeit liegen';
      }
    }

    return {
      valid: Object.keys(fieldErrors).length === 0,
      fieldErrors
    };
  }


  updateEvent(id: number, event: EventDto): Observable<EventDto> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Events aktualisieren');
    }
    return this.httpClient.put<EventDto>(`${this.eventsBaseUri}/${id}`, event);
  }
}


