import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Artist } from '../dtos/artist';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import {EventDto} from "../dtos/event";

@Injectable({
  providedIn: 'root'
})
export class ArtistsService {
  private artistsBaseUri: string = this.globals.backendUri + '/artists';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private authService: AuthService
  ) {}

  getArtists(): Observable<Artist[]> {
    return this.httpClient.get<Artist[]>(this.artistsBaseUri);
  }

  getArtistById(id: number): Observable<Artist> {
    return this.httpClient.get<Artist>(`${this.artistsBaseUri}/${id}`);
  }

  getArtistImage(id: number): Observable<Blob> {
    return this.httpClient.get(`${this.artistsBaseUri}/${id}/image`, { responseType: 'blob' });
  }

  getArtistEvents(id: number): Observable<EventDto[]> {
    return this.httpClient.get<EventDto[]>(`${this.artistsBaseUri}/${id}/events`);
  }

  createArtist(formData: FormData): Observable<Artist> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Artists erstellen');
    }
    return this.httpClient.post<Artist>(this.artistsBaseUri, formData);
  }

  updateArtist(id: number, formData: FormData): Observable<Artist> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Artists aktualisieren');
    }
    return this.httpClient.put<Artist>(`${this.artistsBaseUri}/${id}`, formData);
  }

  deleteArtist(id: number): Observable<void> {
    if (this.authService.getUserRole() !== 'ADMIN') {
      throw new Error('Nur Administratoren können Artists löschen');
    }
    return this.httpClient.delete<void>(`${this.artistsBaseUri}/${id}`);
  }
}
