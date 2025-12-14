import {Injectable} from "@angular/core";
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {EventAutocompleteDto, EventDto, EventTop10Dto, EventType} from "../dtos/event";
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

  getEventAutocompleteByTitle(title:string, limit:number): Observable<EventAutocompleteDto[]>{
    const params = new HttpParams()
      .set("title", title)
      .set("limit", limit);
    return this.httpClient.get<EventAutocompleteDto[]>(`${this.eventsBaseUri}/autocomplete`, {params});
  }

  getTop10Events(eventType: EventType | null): Observable<EventTop10Dto[]> {
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

  createEvent(event: EventDto): Observable<EventDto> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Events erstellen');
    }
    return this.httpClient.post<EventDto>(this.eventsBaseUri, event);
  }

  updateEvent(id: number, event: EventDto): Observable<EventDto> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Events aktualisieren');
    }
    return this.httpClient.put<EventDto>(`${this.eventsBaseUri}/${id}`, event);
  }
}


