import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { ReservationDto } from '../dtos/reservation.dto';
import { ReservationCreateDto } from '../dtos/reservation-create.dto';

@Injectable({ providedIn: 'root' })
export class ReservationsService {

  private baseUri = this.globals.backendUri + '/reservations';

  constructor(private http: HttpClient, private globals: Globals) {}

  create(dto: ReservationCreateDto): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(this.baseUri, dto);
  }
}
