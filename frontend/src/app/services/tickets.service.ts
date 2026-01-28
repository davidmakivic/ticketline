import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { HttpClient } from '@angular/common/http';
import {Observable, shareReplay} from 'rxjs';

import { Ticket, TicketStatus } from '../dtos/ticket';

@Injectable({ providedIn: 'root' })
export class TicketsService {

  private ticketsBaseUri: string = this.globals.backendUri + '/tickets';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals
  ) {}

  getTickets(): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(this.ticketsBaseUri);
  }

  getTicketById(id: number): Observable<Ticket> {
    return this.httpClient.get<Ticket>(`${this.ticketsBaseUri}/${id}`);
  }

  hold(ticketId: number): Observable<Ticket> {
    return this.httpClient.post<Ticket>(`${this.ticketsBaseUri}/${ticketId}/holds`, {});
  }

  releaseHold(ticketId: number): Observable<Ticket> {
    return this.httpClient.delete<Ticket>(`${this.ticketsBaseUri}/${ticketId}/holds/me`);
  }

  getTicketsByPerformance(performanceId: number): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(
      `${this.ticketsBaseUri}?performanceId=${performanceId}`
    );
  }

  getTicketsByPerformanceShared(performanceId: number): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(`${this.ticketsBaseUri}?performanceId=${performanceId}`)
      .pipe(
        shareReplay({refCount: false, scheduler: undefined, bufferSize: 1, windowTime: 5000 }) // Cache für 5 Sekunden
      );
  }



  createTicket(ticket: Ticket): Observable<Ticket> {
    return this.httpClient.post<Ticket>(this.ticketsBaseUri, ticket);
  }
}
