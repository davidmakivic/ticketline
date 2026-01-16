import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';
import { MerchandiseDto } from '../dtos/merchandise';

@Injectable({ providedIn: 'root' })
export class MerchandiseService {
  private baseUri = this.globals.backendUri + '/merchandise';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {}

  getAll(): Observable<MerchandiseDto[]> {
    return this.http.get<MerchandiseDto[]>(this.baseUri);
  }

  getById(id: number): Observable<MerchandiseDto> {
    return this.http.get<MerchandiseDto>(`${this.baseUri}/${id}`);
  }

  getImage(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUri}/${id}/image`, {
      responseType: 'blob'
    });
  }
}
