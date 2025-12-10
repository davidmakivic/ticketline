import {Injectable} from "@angular/core";
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {EventDto} from "../dtos/event";

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


