import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    return this.httpClient.post<Ticket>(`${this.ticketsBaseUri}/${ticketId}/hold`, {});
  }

  releaseHold(ticketId: number): Observable<Ticket> {
    return this.httpClient.delete<Ticket>(`${this.ticketsBaseUri}/${ticketId}/hold`);
  }

  getTicketsByPerformance(performanceId: number): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(`${this.ticketsBaseUri}/performance/${performanceId}`);
  }


  createTicket(ticket: Ticket): Observable<Ticket> {
    return this.httpClient.post<Ticket>(this.ticketsBaseUri, ticket);
  }
}
