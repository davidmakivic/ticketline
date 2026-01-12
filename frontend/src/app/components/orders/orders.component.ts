import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../../services/order.service';
import { OrderDto } from '../../dtos/order.dto';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent {

  loading = false;
  error: string | null = null;
  orders: OrderDto[] = [];

constructor(private orderService: OrdersService) {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = null;

    this.orderService.getAll().subscribe({
      next: data => {
        this.orders = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load orders';
        this.loading = false;
      }
    });
  }

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2) + ' €';
  }
}
