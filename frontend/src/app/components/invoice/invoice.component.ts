import { Component, Renderer2, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { OrderDto } from '../../dtos/order.dto';
import { CartItem } from '../../dtos/cart-item';
import { TicketsService } from '../../services/tickets.service';
import { Ticket } from '../../dtos/ticket';

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

type InvoiceState = {
  order?: OrderDto;
  items?: CartItem[];
  payment?: string;
  customerName?: string;
};

@Component({
  selector: 'app-invoice',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss']
})
export class InvoiceComponent implements OnDestroy {

  order?: OrderDto;
  items: CartItem[] = [];
  payment?: string;

  invoiceNo = '';
  invoiceDateStr = '';
  serviceDateStr = '';
  customerName = 'Kunde';

  private ticketPriceById = new Map<number, number>();

  seller = {
    name: 'Ticketline GmbH',
    street: 'Musterstraße 1',
    zipCity: '1010 Wien',
    country: 'Österreich',
    email: 'support@ticketline.example',
    phone: '+43 1 234 567'
  };

  constructor(
    private router: Router,
    private renderer: Renderer2,
    private ticketsService: TicketsService
  ) {
    const state = history.state as InvoiceState;

    this.order = state.order;
    this.items = state.items ?? [];
    this.payment = state.payment;
    this.customerName = state.customerName ?? 'Kunde';

    if (this.order) {
      const dt = this.order.createdAt ? new Date(this.order.createdAt) : new Date();
      this.invoiceNo = this.buildInvoiceNumber(this.order.id, dt);
      this.invoiceDateStr = this.formatDate(dt);
      this.serviceDateStr = this.formatDate(dt);
    }

    if (this.order?.ticketIds?.length) {
      forkJoin(
        this.order.ticketIds.map(id =>
          this.ticketsService.getTicketById(id).pipe(
            catchError(() => of(null))
          )
        )
      ).pipe(
        map(list => list.filter((t): t is Ticket => t !== null))
      ).subscribe(tickets => {
        tickets.forEach(t =>
          this.ticketPriceById.set(t.id, t.priceFinalCents ?? 0)
        );
      });
    }

    this.renderer.addClass(document.body, 'invoice-print');
  }

  ngOnDestroy(): void {
    this.renderer.removeClass(document.body, 'invoice-print');
  }



  print(): void {
    window.print();
  }

  backToHome(): void {
    this.router.navigate(['/']);
  }

  paymentLabel(): string {
    switch (this.payment) {
      case 'card': return 'Kreditkarte';
      case 'paypal': return 'PayPal';
      case 'klarna': return 'Klarna';
      case 'applepay': return 'Apple Pay';
      default: return 'Unbekannt';
    }
  }

  itemTitle(item: any): string {
    return item.title ?? item.name ?? item.performanceTitle ?? item.eventTitle ?? 'Ticket';
  }

  itemDetails(item: any): string {
    const venue = item.venueName ?? item.venue ?? null;
    const date = item.performanceDate ?? item.date ?? null;
    const seat = item.seatLabel ?? item.seat ?? null;

    const parts: string[] = [];
    if (venue) parts.push(String(venue));
    if (date) parts.push(String(date));
    if (seat) parts.push(`Sitz: ${seat}`);

    return parts.length ? parts.join(' • ') : 'Sitzplatz / Eintritt';
  }

  itemQuantity(item: any): number {
    return item.quantity ?? 1;
  }

  itemUnitPriceCents(item: any): number {
    return this.ticketPriceById.get(item.ticketId) ?? 0;
  }

  itemTotalCents(item: any): number {
    return this.itemUnitPriceCents(item) * this.itemQuantity(item);
  }

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }


  private buildInvoiceNumber(orderId: number, date: Date): string {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `TL-${yyyy}${mm}${dd}-${String(orderId).padStart(5, '0')}`;
  }

  private formatDate(d: Date): string {
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const yyyy = d.getFullYear();
    const hh = String(d.getHours()).padStart(2, '0');
    const mi = String(d.getMinutes()).padStart(2, '0');
    return `${dd}.${mm}.${yyyy} ${hh}:${mi}`;
  }
}
