import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { PerformanceDto } from '../dtos/performanceDto';
import { PagedResult, EventTypeDto } from '../dtos/event';

@Injectable({ providedIn: 'root' })
export class PerformancesService {
  private baseUri = this.globals.backendUri + '/performances';
  constructor(private http: HttpClient, private globals: Globals) {}

  getById(id: number): Observable<PerformanceDto> {
    return this.http.get<PerformanceDto>(`${this.baseUri}/${id}`);
  }

  create(performance: Partial<PerformanceDto>): Observable<PerformanceDto> {
    return this.http.post<PerformanceDto>(this.baseUri, performance);
  }

  getAll(page: number = 0, size: number = 10): Observable<PagedResult<PerformanceDto>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResult<PerformanceDto>>(this.baseUri, { params });
  }

  searchAdvanced(filters: {
    title?: string;
    artist?: string;
    location?: string;
    eventType?: EventTypeDto;
    startDate?: Date;
    durationMinutes?: number;
  }, page: number = 0, size: number = 10): Observable<PagedResult<PerformanceDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters.title) params = params.set('title', filters.title);
    if (filters.artist) params = params.set('artist', filters.artist);
    if (filters.location) params = params.set('location', filters.location);
    if (filters.eventType) params = params.set('eventType', filters.eventType);
    if (filters.startDate) params = params.set('startDate', this.formatDate(filters.startDate));
    if (filters.durationMinutes) params = params.set('durationMinutes', filters.durationMinutes.toString());

    return this.http.get<PagedResult<PerformanceDto>>(`${this.baseUri}`, { params });
  }

  private formatDate(date: Date): string {
    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    const year = localDate.getFullYear();
    const month = String(localDate.getMonth() + 1).padStart(2, '0');
    const day = String(localDate.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

}
