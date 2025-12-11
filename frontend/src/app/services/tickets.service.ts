import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { Ticket } from '../dtos/ticket';
import { TicketStatus } from '../dtos/ticket';

@Injectable({ providedIn: 'root' })
export class TicketsService {

  private ticketsBaseUri: string = this.globals.backendUri + '/tickets';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private authService: AuthService
  ) {}

  getTickets(): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(this.ticketsBaseUri);
  }

  getTicketById(id: number): Observable<Ticket> {
    return this.httpClient.get<Ticket>(`${this.ticketsBaseUri}/${id}`);
  }

  createTicket(ticket: Ticket): Observable<Ticket> {
    return this.httpClient.post<Ticket>(this.ticketsBaseUri, ticket);
  }

  getTicketsByPerformance(performanceId: number): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(
      `${this.ticketsBaseUri}/performance/${performanceId}`
    );
  }

  updateStatus(id: number, status: TicketStatus, version: number): Observable<Ticket> {
    return this.httpClient.put<Ticket>(
      `${this.ticketsBaseUri}/${id}/status`,
      { status, version }
    );
  }
}
