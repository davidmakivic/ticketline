import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { Seat } from '../dtos/seat';

@Injectable({ providedIn: 'root' })
export class SeatsService {
  private baseUri = this.globals.backendUri + '/seats';
  constructor(private http: HttpClient, private globals: Globals) {}

  getBySectorId(sectorId: number): Observable<Seat[]> {
    return this.http.get<Seat[]>(`${this.baseUri}/sector/${sectorId}`);
  }
}
