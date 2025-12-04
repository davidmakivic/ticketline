import {Injectable} from "@angular/core";
import {Globals} from '../global/globals';
import {HttpClient} from "@angular/common/http";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {Event} from "../dtos/event";

@Injectable({providedIn: "root"})
export class EventsService {
  private eventsBaseUri: string = this.globals.backendUri + "/events";

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {}

  getEvents(): Observable<Event[]> {
    return this.httpClient.get<Event[]>(this.eventsBaseUri);
  }

  getEventById(id: number): Observable<Event> {
    return this.httpClient.get<Event>(`${this.eventsBaseUri}/${id}`);
  }

  createEvent(event: Event): Observable<Event> {
    return this.httpClient.post<Event>(this.eventsBaseUri, event);
  }

}



