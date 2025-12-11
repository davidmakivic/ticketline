import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { Performance } from '../dtos/performance';

@Injectable({ providedIn: 'root' })
export class PerformancesService {
  private baseUri = this.globals.backendUri + '/performances';
  constructor(private http: HttpClient, private globals: Globals) {}

  getById(id: number): Observable<Performance> {
    return this.http.get<Performance>(`${this.baseUri}/${id}`);
  }
}
