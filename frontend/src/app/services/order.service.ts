import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { OrderDto } from '../dtos/order.dto';
import { CartItem } from '../dtos/cart-item';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class OrdersService {

  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/orders`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<OrderDto[]> {
    return this.http.get<OrderDto[]>(this.baseUrl);
  }

  createFromCart(items: CartItem[]): Observable<OrderDto> {
    return this.http.post<OrderDto>(this.baseUrl, {
      ticketIds: items.map(i => i.id)
    });
  }
}
