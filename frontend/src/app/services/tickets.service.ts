import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Ticket, TicketStatus } from '../dtos/ticket';
import { TicketStatusUpdateDto } from '../dtos/ticket-status-update';

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

  reserve(ticketId: number, version: number): Observable<Ticket> {
    return this.httpClient.put<Ticket>(
      `${this.ticketsBaseUri}/${ticketId}/reserve`,
      { status: TicketStatus.RESERVED, version }
    );
  }

  release(ticketId: number, version: number) {
    return this.httpClient.put<Ticket>(
      `${this.ticketsBaseUri}/${ticketId}/release`,
      { status: TicketStatus.AVAILABLE, version }
    );
  }

  getTicketsByPerformance(performanceId: number): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(`${this.ticketsBaseUri}/performance/${performanceId}`);
  }

  updateStatus(ticketId: number, body: TicketStatusUpdateDto): Observable<Ticket> {
    return this.httpClient.put<Ticket>(`${this.ticketsBaseUri}/${ticketId}/status`, body);
  }

  createTicket(ticket: Ticket): Observable<Ticket> {
    return this.httpClient.post<Ticket>(this.ticketsBaseUri, ticket);
  }
}
