import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import {User, UserDto, UserRegisterDto, UserUpdateDto} from "../dtos/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userBaseUrl: string = this.globals.backendUri + '/users';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private authService: AuthService
  ) {}

  createUser(dto: UserRegisterDto): Observable<void> {
    return this.httpClient.post<void>(this.userBaseUrl,dto)
  }

  getUser(): Observable<UserDto> {
    return this.httpClient.get<UserDto>(this.userBaseUrl + '/me');
  }

  updateUser(payload: UserUpdateDto): Observable<void> {
    return this.httpClient.put<void>(this.userBaseUrl + '/me', payload)
  }
}
