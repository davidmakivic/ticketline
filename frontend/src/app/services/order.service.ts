import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { OrderDto } from '../dtos/order.dto';
import { CartItem, isMerchItem, isTicketItem } from '../dtos/cart-item';
import { environment } from '../../environments/environment';

export interface CancellationResultDto {
  orderId: number;
  cancelledTicketIds: number[];
  refundTotalCents: number;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class OrdersService {

  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/orders`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<OrderDto[]> {
    return this.http.get<OrderDto[]>(this.baseUrl);
  }

  createFromCart(items: CartItem[]): Observable<OrderDto> {
    const ticketIds = items.filter(isTicketItem).map(i => i.ticketId);

    const merchItems = items
      .filter(isMerchItem)
      .map(m => ({
        variantId: Number(m.variantId),
        quantity: Number(m.quantity)
      }))
      .filter(x => Number.isFinite(x.variantId) && Number.isFinite(x.quantity) && x.quantity > 0);

    return this.http.post<OrderDto>(this.baseUrl, {
      ticketIds,
      merchItems
    });
  }

  cancelTickets(orderId: number, ticketIds: number[]): Observable<CancellationResultDto> {
    return this.http.post<CancellationResultDto>(
      `${this.baseUrl}/${orderId}/cancel`,
      { ticketIds }
    );
  }
}
