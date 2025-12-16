import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../../services/order.service';
import { OrderDto } from '../../dtos/order.dto';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { TicketsService } from '../../services/tickets.service';
import { PerformancesService } from '../../services/performances.service';





@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent {
  eventTitleByOrderId: Record<number, string> = {};
  loading = false;
  error: string | null = null;
  orders: OrderDto[] = [];

constructor(private orderService: OrdersService, private router: Router,   private ticketsService: TicketsService,
  private performancesService: PerformancesService) {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = null;

    this.orderService.getAll().subscribe({
      next: data => {
        this.orders = data;
        this.loadEventTitles(data);
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load orders';
        this.loading = false;
      }
    });
  }

cancelOrder(o: OrderDto) {
  if (!o.ticketIds || o.ticketIds.length === 0) {
    alert('Keine Tickets zum Stornieren');
    return;
  }

  if (!confirm(`Bestellung #${o.id} wirklich stornieren?`)) return;

  this.orderService.cancelTickets(o.id, o.ticketIds).subscribe({
    next: (cancellation) => {
      this.router.navigate(['/storno-invoice', o.id], {
        state: {
          cancellation,
          customerName: 'Kunde',
        }
      });
    },
    error: (e) => {
      console.error(e);
      alert('Stornierung fehlgeschlagen');
    }
  });
}
  toEuro(cents: number): string {
    return (cents / 100).toFixed(2) + ' €';
  }

private loadEventTitles(orders: OrderDto[]) {
  this.eventTitleByOrderId = {};

  const pairs: { orderId: number; ticketId: number }[] = [];
  for (const o of orders) {
    for (const tid of (o.ticketIds ?? [])) {
      pairs.push({ orderId: o.id, ticketId: tid });
    }
  }
  if (pairs.length === 0) return;

  forkJoin(
    pairs.map(p =>
      this.ticketsService.getTicketById(p.ticketId).pipe(
        map(t => ({ orderId: p.orderId, performanceId: t.performanceId })),
        catchError(() => of(null))
      )
    )
  ).pipe(
    map(list => list.filter((x): x is { orderId: number; performanceId: number } => x !== null))
  ).subscribe(orderPerfList => {
    const perfIds = Array.from(new Set(orderPerfList.map(x => x.performanceId)));
    if (perfIds.length === 0) return;

    forkJoin(
      perfIds.map(id =>
        this.performancesService.getById(id).pipe(
          map(p => ({
            id,
            title: (p as any)?.title ?? (p as any)?.eventTitle ?? (p as any)?.name ?? `Performance #${id}`
          })),
          catchError(() => of({ id, title: `Performance #${id}` }))
        )
      )
    ).subscribe(perfs => {
      const titleByPerf = new Map<number, string>();
      perfs.forEach(p => titleByPerf.set(p.id, p.title));

      const byOrder: Record<number, string[]> = {};
      orderPerfList.forEach(x => {
        const title = titleByPerf.get(x.performanceId) ?? `Performance #${x.performanceId}`;
        (byOrder[x.orderId] ||= []).push(title);
      });

      Object.entries(byOrder).forEach(([oid, titles]) => {
        this.eventTitleByOrderId[Number(oid)] = Array.from(new Set(titles)).join(' • ');
      });
    });
  });
}
}
