import {Injectable} from "@angular/core";
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {EventAutocompleteDto, EventDto, EventTop10Dto, EventTypeDto} from "../dtos/event";
import {ArtistDataDto} from "../dtos/artist";

@Injectable({providedIn: "root"})
export class EventsService {
  private eventsBaseUri: string = this.globals.backendUri + "/events";

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {}

  getEvents(): Observable<EventDto[]> {
    return this.httpClient.get<EventDto[]>(this.eventsBaseUri);
  }

  searchEventsByTitle(title: string): Observable<EventDto[]> {
    const params = new HttpParams().set('title', title);
    return this.httpClient.get<EventDto[]>(`${this.eventsBaseUri}/query`, { params });
  }

  searchEventsByFilters(title: string, artist: string, location: string): Observable<EventDto[]> {
    return this.httpClient.get<EventDto[]>(`${this.eventsBaseUri}/query`, {
      params: {
        title: title || '',
        artist: artist || '',
        location: location || ''
      }
    });
  }

  searchAdvanced(filters: {
    title?: string;
    artist?: string;
    location?: string;
    eventType?: string;
    startDate?: Date;
    durationMinutes?: number;
  }): Observable<EventDto[]> {
    let params = new HttpParams();

    if (filters.title) {
      params = params.set('title', filters.title);
    }
    if (filters.artist) {
      params = params.set('artist', filters.artist);
    }
    if (filters.location) {
      params = params.set('location', filters.location);
    }
    if (filters.eventType) {
      params = params.set('eventType', filters.eventType);
    }
    if (filters.startDate) {
      params = params.set('startDate', this.formatDate(filters.startDate));
    }
    if (filters.durationMinutes) {
      params = params.set('durationMinutes', filters.durationMinutes.toString());
    }

    return this.httpClient.get<EventDto[]>(`${this.eventsBaseUri}/query`, { params });
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

  updateEvent(id: number, event: EventDto): Observable<EventDto> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Events aktualisieren');
    }
    return this.httpClient.put<EventDto>(`${this.eventsBaseUri}/${id}`, event);
  }
}


