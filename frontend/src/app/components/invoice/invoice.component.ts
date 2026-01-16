import { Component, Renderer2, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { OrderDto, OrderMerchItemDto } from '../../dtos/order.dto';
import { CartItem, isMerchItem, isTicketItem } from '../../dtos/cart-item';
import { TicketsService } from '../../services/tickets.service';
import { Ticket } from '../../dtos/ticket';
import { OrdersService } from '../../services/order.service';

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { PerformancesService } from '../../services/performances.service';
import { EventsService } from '../../services/events.service';

type InvoiceType = 'tickets' | 'merch';

type InvoiceState = {
  order?: OrderDto;
  items?: CartItem[];
  payment?: string;
  customerName?: string;
  eventTitle?: string;
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
  tickets: Ticket[] = [];
  eventTitle = '';

  viewType: InvoiceType = 'tickets';

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
    private route: ActivatedRoute,
    private renderer: Renderer2,
    private ticketsService: TicketsService,
    private ordersService: OrdersService,
    private performancesService: PerformancesService,
    private eventsService: EventsService
  ) {
    const state = history.state as InvoiceState;

    this.order = state.order;
    this.items = state.items ?? [];
    this.payment = state.payment;
    this.customerName = state.customerName ?? 'Kunde';
    this.eventTitle = state.eventTitle ?? '';

    const qpType = (this.route.snapshot.queryParamMap.get('type') ?? 'tickets') as InvoiceType;
    this.viewType = qpType === 'merch' ? 'merch' : 'tickets';

    const idFromRoute = Number(this.route.snapshot.paramMap.get('id'));

    if (!this.order && Number.isFinite(idFromRoute) && idFromRoute > 0) {
      this.ordersService.getAll().pipe(
        map(list => (list ?? []).find(x => x.id === idFromRoute))
      ).subscribe({
        next: (o) => {
          if (!o) {
            return;
          }

          this.order = o;

          if (!this.payment) {
            try {
              this.payment = localStorage.getItem(`order-payment-${o.id}`) ?? undefined;
            } catch {
              /* ignore */
            }
          }

          if (!this.items.length) {
            const tItems = (o.ticketIds ?? []).map(tid => ({
              kind: 'ticket',
              ticketId: tid,
              addedAt: ''
            } as any));

            const mItems = (o.merchItems ?? []).map(mi => ({
              kind: 'merch',
              merchandiseId: mi.merchandiseId,
              variantId: mi.variantId,
              name: mi.merchandiseName,
              size: mi.size ?? null,
              unitPriceCents: Number(mi.unitPriceCents ?? 0),
              quantity: Number(mi.quantity ?? 0),
              addedAt: ''
            } as any));

            this.items = [...tItems, ...mItems];
          }

          this.initInvoiceDates(o);
          this.loadTickets(o);
        },
        error: () => {
          /* ignore */
        }
      });
    } else if (this.order) {
      if (!this.payment) {
        try {
          this.payment = localStorage.getItem(`order-payment-${this.order.id}`) ?? undefined;
        } catch {
          /* ignore */
        }
      }

      if (!this.items.length) {
        const o = this.order;

        const tItems = (o.ticketIds ?? []).map(tid => ({
          kind: 'ticket',
          ticketId: tid,
          addedAt: ''
        } as any));

        const mItems = (o.merchItems ?? []).map(mi => ({
          kind: 'merch',
          merchandiseId: mi.merchandiseId,
          variantId: mi.variantId,
          name: mi.merchandiseName,
          size: mi.size ?? null,
          unitPriceCents: Number(mi.unitPriceCents ?? 0),
          quantity: Number(mi.quantity ?? 0),
          addedAt: ''
        } as any));

        this.items = [...tItems, ...mItems];
      }

      this.initInvoiceDates(this.order);
      this.loadTickets(this.order);
    }

    this.renderer.addClass(document.body, 'invoice-print');
  }

  ngOnDestroy(): void {
    this.renderer.removeClass(document.body, 'invoice-print');
  }

  setType(t: InvoiceType): void {
    this.viewType = t;
    const id = this.order?.id ?? Number(this.route.snapshot.paramMap.get('id'));

    if (id) {
      this.router.navigate(['/invoice', id], {
        queryParams: { type: t },
        replaceUrl: true,
        state: history.state
      });
    }
  }

  print(): void {
    window.print();
  }

  backToHome(): void {
    this.router.navigate(['/']);
  }

  paymentLabel(): string {
    switch (this.payment) {
      case 'card':
        return 'Kreditkarte';
      case 'paypal':
        return 'PayPal';
      case 'klarna':
        return 'Klarna';
      case 'applepay':
        return 'Apple Pay';
      default:
        return 'Unbekannt';
    }
  }

  ticketItems(): any[] {
    return this.items.filter(isTicketItem);
  }

  itemTitle(item: any): string {
    return item.title
      ?? item.name
      ?? item.performanceTitle
      ?? item.eventTitle
      ?? this.eventTitle
      ?? 'Ticket';
  }

  itemDetails(item: any): string {
    const venue = item.venueName ?? item.venue ?? null;
    const date = item.performanceDate ?? item.date ?? null;
    const seat = item.seatLabel ?? item.seat ?? null;

    const parts: string[] = [];

    if (venue) {
      parts.push(String(venue));
    }

    if (date) {
      parts.push(String(date));
    }

    if (seat) {
      parts.push(`Sitz: ${seat}`);
    }

    return parts.length ? parts.join(' • ') : 'Sitzplatz / Eintritt';
  }

  itemUnitPriceCents(item: any): number {
    return this.ticketPriceById.get(item.ticketId) ?? 0;
  }

  ticketTotalCents(): number {
    return this.ticketItems()
      .reduce((s, it) => s + this.itemUnitPriceCents(it), 0);
  }

  merchItems(): OrderMerchItemDto[] {
    return this.order?.merchItems ?? [];
  }

  merchTotalCents(): number {
    return this.merchItems()
      .reduce((s, m) => s + (Number(m.unitPriceCents ?? 0) * Number(m.quantity ?? 0)), 0);
  }

  toEuro(cents: number): string {
    return `${(cents / 100).toFixed(2).replace('.', ',')} €`;
  }

  private initInvoiceDates(order: OrderDto): void {
    const dt = order.createdAt ? new Date(order.createdAt) : new Date();
    this.invoiceNo = this.buildInvoiceNumber(order.id, dt);
    this.invoiceDateStr = this.formatDate(dt);
    this.serviceDateStr = this.formatDate(dt);
  }

  private loadTickets(order: OrderDto): void {
    if (!order?.ticketIds?.length) {
      this.tickets = [];
      return;
    }

    forkJoin(
      order.ticketIds.map(id =>
        this.ticketsService.getTicketById(id).pipe(
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((t): t is Ticket => t !== null))
    ).subscribe(tickets => {
      this.tickets = tickets;

      tickets.forEach(t => {
        this.ticketPriceById.set((t as any).id, (t as any).priceFinalCents ?? 0);
      });

      if (!this.eventTitle && tickets.length) {
        const t: any = tickets[0];
        this.eventTitle =
          t?.performanceTitle ??
          t?.eventTitle ??
          t?.performance?.title ??
          t?.performance?.eventTitle ??
          t?.performance?.event?.title ??
          t?.event?.title ??
          t?.event?.name ??
          t?.title ??
          '';
      }

      if (!this.eventTitle && tickets.length) {
        const first: any = tickets[0];
        const perfId = first?.performanceId ?? first?.performance?.id ?? null;

        if (perfId != null) {
          this.performancesService.getById(perfId).pipe(
            catchError(() => of(null))
          ).subscribe((p: any) => {
            const title =
              p?.title ??
              p?.eventTitle ??
              p?.event?.title ??
              p?.event?.name ??
              p?.name ??
              null;

            if (title) {
              this.eventTitle = title;
              return;
            }

            const eventId = p?.eventId ?? p?.event?.id ?? null;

            if (eventId != null) {
              this.eventsService.getEventById(eventId).pipe(
                catchError(() => of(null))
              ).subscribe((e: any) => {
                this.eventTitle = e?.title ?? e?.name ?? this.eventTitle ?? '';
              });
            }
          });
        }
      }

      const shouldPrint = this.route.snapshot.queryParamMap.get('print') === '1';

      if (shouldPrint) {
        setTimeout(() => window.print(), 200);
      }
    });
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

  ticketSeatLabel(t: any): string {
    const sector = t?.sectorName ?? t?.seat?.sector?.name ?? null;
    const row = t?.seatRow ?? t?.seat?.row ?? t?.row ?? null;
    const number = t?.seatNumber ?? t?.seat?.number ?? t?.number ?? null;

    const seat =
      row != null && number != null
        ? `Reihe ${row}, Sitz ${number}`
        : t?.seatId != null
          ? `Sitz #${t.seatId}`
          : null;

    if (sector && seat) {
      return `${sector} • ${seat}`;
    }

    if (seat) {
      return seat;
    }

    return 'Freie Platzwahl';
  }

  hasTickets(): boolean {
    return (this.order?.ticketIds?.length ?? 0) > 0;
  }

  hasMerch(): boolean {
    return (this.order?.merchItems?.length ?? 0) > 0;
  }
}
