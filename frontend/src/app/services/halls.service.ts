import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { Hall } from '../dtos/hall';

@Injectable({ providedIn: 'root' })
export class HallsService {
  private baseUri = this.globals.backendUri + '/halls';
  constructor(private http: HttpClient, private globals: Globals) {}

  getById(id: number): Observable<Hall> {
    return this.http.get<Hall>(`${this.baseUri}/${id}`);
  }
}
