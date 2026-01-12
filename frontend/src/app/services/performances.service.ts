import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { PerformanceDto } from '../dtos/performanceDto';

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
}
