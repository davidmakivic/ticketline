import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { ReservationDto } from '../dtos/reservation.dto';
import { ReservationCreateDto } from '../dtos/reservation-create.dto';
import { environment } from '../../environments/environment';


@Injectable({ providedIn: 'root' })
export class ReservationsService {

  private readonly baseUri = `${environment.apiBaseUrl}/api/v1/reservations`;

  constructor(private http: HttpClient, private globals: Globals) {}

  create(dto: ReservationCreateDto): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(this.baseUri, dto);
  }

getAll(): Observable<ReservationDto[]> {
  return this.http.get<ReservationDto[]>(this.baseUri);
}
delete(reservationId: number) {
  return this.http.delete<void>(`${this.baseUri}/${reservationId}`);
}

}
