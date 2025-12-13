import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { Venue } from '../dtos/venue';

@Injectable({ providedIn: 'root' })
export class VenuesService {
  private baseUri = this.globals.backendUri + '/venues';
  constructor(private http: HttpClient, private globals: Globals) {}

  getById(id: number): Observable<Venue> {
    return this.http.get<Venue>(`${this.baseUri}/${id}`);
  }
}
