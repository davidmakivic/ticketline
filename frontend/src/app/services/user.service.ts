import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import {PageResponse} from "../dtos/page-response";
import {PasswordChangeDto, User, UserDto, UserRegisterDto, UserUpdateDto} from "../dtos/user";

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

  getUsers(page: number, size: number, email?: string): Observable<PageResponse<UserDto>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (email && email.trim().length > 0) {
      params = params.set('email', email.trim());
    }

    return this.httpClient.get<PageResponse<UserDto>>(this.userBaseUrl, { params });
  }

// block/unblock (void Endpoint => body null!)
  blockUser(userId: number) {
    return this.httpClient.put<void>(`${this.userBaseUrl}/${userId}/block`, null);
  }

  unblockUser(userId: number) {
    return this.httpClient.put<void>(`${this.userBaseUrl}/${userId}/unblock`, null);
  }

  changePassword(dto: PasswordChangeDto) {
    return this.httpClient.post<void>(`${this.userBaseUrl}/changePassword`, dto);
  }

  getUser(): Observable<UserDto> {
    return this.httpClient.get<UserDto>(this.userBaseUrl + '/me');
  }

  updateUser(payload: UserUpdateDto): Observable<void> {
    return this.httpClient.put<void>(this.userBaseUrl + '/me', payload)
  }
}
