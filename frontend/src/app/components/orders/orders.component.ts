import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../../services/order.service';
import { OrderDto } from '../../dtos/order.dto';
import { Router } from '@angular/router';

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { TicketsService } from '../../services/tickets.service';
import { PerformancesService } from '../../services/performances.service';
import { EventsService } from '../../services/events.service';

import { ReservationsService } from '../../services/reservations.service';
import { ReservationDto } from '../../dtos/reservation.dto';
import { CartService } from '../../services/cart.service';

type OrderMetaCache = {
  eventTitle: string;
  seats: string[];
  savedAt: string;
};

type ReservationTicketLine = {
  ticketId: number;
  title: string;
  seatLabel: string;
  priceCents: number;
};

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent {

  eventTitleByOrderId: Record<number, string> = {};
  seatsByOrderId: Record<number, string[]> = {};
  ticketCountByOrderId: Record<number, number> = {};

  orders: OrderDto[] = [];
  loading = false;
  error: string | null = null;

  confirmOpen = false;
  confirmOrder: OrderDto | null = null;

  reservations: ReservationDto[] = [];
  showReservations = true;

  reservationSeatsById: Record<number, string[]> = {};
  reservationTicketCountById: Record<number, number> = {};
  eventTitleByReservationId: Record<number, string> = {};

  reservationLinesById: Record<number, ReservationTicketLine[]> = {};
  reservationTotalCentsById: Record<number, number> = {};

  reservationSelectedById: Record<number, Record<number, boolean>> = {};

  constructor(
    private orderService: OrdersService,
    private router: Router,
    private ticketsService: TicketsService,
    private performancesService: PerformancesService,
    private eventsService: EventsService,
    private reservationsService: ReservationsService,
    private cart: CartService
  ) {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = null;

    this.orderService.getAll().subscribe({
      next: data => {
        this.orders = data ?? [];

        this.cleanupMetaForActiveOrders(this.orders);

        this.loadEventTitles(this.orders);
        this.loadSeatsForOrders(this.orders);

        this.applyCachedMeta(this.orders);

        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load orders';
        this.loading = false;
      }
    });

    this.reservationsService.getAll().subscribe({
      next: res => {
        this.reservations = res ?? [];

        for (const r of this.reservations) {
          this.reservationSelectedById[r.id] ||= {};
          for (const tid of (r.ticketIds ?? [])) {
            if (this.reservationSelectedById[r.id][tid] === undefined) {
              this.reservationSelectedById[r.id][tid] = true;
            }
          }
        }

        for (const ridStr of Object.keys(this.reservationSelectedById)) {
          const rid = Number(ridStr);
          if (!this.reservations.some(r => r.id === rid)) {
            delete this.reservationSelectedById[rid];
          }
        }

        this.loadEventTitlesForReservations(this.reservations);
        this.loadSeatsForReservations(this.reservations);
        this.loadReservationDetails(this.reservations);
      },
      error: () => {
        this.reservations = [];
      }
    });
  }


  isCancelled(o: OrderDto): boolean {
    return (o.ticketIds?.length ?? 0) === 0 || (o.totalPriceCents ?? 0) === 0;
  }

  statusLabel(o: OrderDto): string {
    return this.isCancelled(o) ? 'Storniert' : 'Gekauft';
  }

  orderTitle(o: OrderDto): string {
    return this.eventTitleByOrderId[o.id]
      ?? (this.isCancelled(o) ? this.loadOrderMeta(o.id)?.eventTitle : null)
      ?? 'Unbekannte Veranstaltung';
  }

  orderSeats(o: OrderDto): string[] {
    const direct = this.seatsByOrderId[o.id];
    if (direct && direct.length) return direct;

    if (this.isCancelled(o)) {
      return this.loadOrderMeta(o.id)?.seats ?? [];
    }
    return [];
  }

  cancelOrder(o: OrderDto) {
    this.openCancelConfirm(o);
  }

  openCancelConfirm(o: OrderDto) {
    if (!o.ticketIds || o.ticketIds.length === 0) {
      alert('Keine Tickets zum Stornieren');
      return;
    }
    this.confirmOrder = o;
    this.confirmOpen = true;
  }

  closeCancelConfirm() {
    this.confirmOpen = false;
    this.confirmOrder = null;
  }

  doCancelConfirmed() {
    const o = this.confirmOrder;
    if (!o) return;

    const ticketIds = o.ticketIds ?? [];
    if (ticketIds.length === 0) {
      alert('Keine Tickets zum Stornieren');
      return;
    }

    this.closeCancelConfirm();

    forkJoin(
      ticketIds.map(id =>
        this.ticketsService.getTicketById(id).pipe(
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((t): t is any => t !== null))
    ).subscribe(preTickets => {

      const seats = Array.from(new Set(preTickets.map(t => this.ticketSeatLabel(t))));

      const first: any = preTickets[0] ?? null;
      const eventTitle =
        this.eventTitleByOrderId[o.id] ??
        first?.performanceTitle ??
        first?.eventTitle ??
        first?.performance?.title ??
        first?.performance?.event?.title ??
        first?.event?.title ??
        first?.title ??
        'Unbekannte Veranstaltung';

      this.saveOrderMeta(o.id, eventTitle, seats);

      this.orderService.cancelTickets(o.id, ticketIds).subscribe({
        next: (cancellation) => {
          o.ticketIds = [];
          o.totalPriceCents = 0;
          this.ticketCountByOrderId[o.id] = 0;
          this.seatsByOrderId[o.id] = [];

          this.router.navigate(['/storno-invoice', o.id], {
            state: {
              cancellation,
              customerName: 'Kunde',
              eventTitle,
              seats
            }
          });
        },
        error: (e) => {
          console.error(e);
          alert('Stornierung fehlgeschlagen');
        }
      });
    });
  }

  openInvoice(o: OrderDto) {
    if (this.isCancelled(o)) return;

    this.router.navigate(['/invoice', o.id], {
      state: {
        order: o,
        items: (o.ticketIds ?? []).map(id => ({ ticketId: id })),
        payment: 'card',
        customerName: 'Kunde',
        eventTitle: this.eventTitleByOrderId[o.id] ?? ''
      }
    });
  }

  openStorno(o: OrderDto) {
    if (!this.isCancelled(o)) return;

    const meta = this.loadOrderMeta(o.id);

    this.router.navigate(['/storno-invoice', o.id], {
      state: {
        customerName: 'Kunde',
        originalInvoiceNo: '',
        eventTitle: meta?.eventTitle ?? this.eventTitleByOrderId[o.id] ?? '',
        seats: meta?.seats ?? this.seatsByOrderId[o.id] ?? []
      }
    });
  }



  buyReservation(r: ReservationDto) {
    const allIds = r.ticketIds ?? [];
    if (!allIds.length) return;

    const selMap = this.reservationSelectedById[r.id] ?? {};

    const selectedIds = allIds.filter(tid => (selMap[tid] ?? true) === true);

    if (selectedIds.length === 0) {
      alert('Bitte wähle mindestens ein Ticket aus.');
      return;
    }

    this.cart.clear();

    sessionStorage.setItem('reservation.pay.rid', String(r.id));
    sessionStorage.setItem('reservation.pay.allIds', JSON.stringify(allIds));
    sessionStorage.setItem('reservation.pay.selectedIds', JSON.stringify(selectedIds));

    selectedIds.forEach(tid => this.cart.addTicket(tid));
    this.router.navigate(['/checkout']);
  }

  deleteReservation(r: ReservationDto) {
    const ids = r.ticketIds ?? [];

    const ok = confirm(`Reservierung #${r.id} wirklich löschen?`);
    if (!ok) return;

    if (!ids.length) {
      this.reservationsService.delete(r.id).subscribe({
        next: () => this.load(),
        error: e => {
          console.error(e);
          alert('Reservierung konnte nicht gelöscht werden');
        }
      });
      return;
    }

    forkJoin(
      ids.map(tid => this.ticketsService.releaseHold(tid).pipe(
        catchError(() => of(null))
      ))
    ).subscribe({
      next: () => {
        this.reservationsService.delete(r.id).subscribe({
          next: () => this.load(),
          error: e => {
            console.error(e);
            alert('Reservierung konnte nicht gelöscht werden');
          }
        });
      },
      error: e => {
        console.error(e);
        this.reservationsService.delete(r.id).subscribe({
          next: () => this.load(),
          error: err => {
            console.error(err);
            alert('Reservierung konnte nicht gelöscht werden');
          }
        });
      }
    });
  }


  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }

  ticketSeatLabel(t: any): string {
    const sector = t?.sectorName ?? t?.seat?.sector?.name ?? null;

    const row = t?.seatRow ?? t?.seat?.row ?? t?.row ?? null;
    const number = t?.seatNumber ?? t?.seat?.number ?? t?.number ?? null;

    const seat = (row != null && number != null)
      ? `Reihe ${row}, Sitz ${number}`
      : (t?.seatId != null ? `Sitz #${t.seatId}` : null);

    if (sector && seat) return `${sector} • ${seat}`;
    if (seat) return seat;
    return 'Freie Platzwahl';
  }


  private loadReservationDetails(reservations: ReservationDto[]) {
    this.reservationLinesById = {};
    this.reservationTotalCentsById = {};

    const pairs: { rid: number; tid: number }[] = [];
    for (const r of reservations) {
      for (const tid of (r.ticketIds ?? [])) {
        pairs.push({ rid: r.id, tid });
      }
    }
    if (!pairs.length) return;

    forkJoin(
      pairs.map(p =>
        this.ticketsService.getTicketById(p.tid).pipe(
          map(t => ({ rid: p.rid, tid: p.tid, t })),
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((x): x is { rid: number; tid: number; t: any } => x !== null && x.t != null))
    ).subscribe(list => {
      for (const x of list) {
        const t = x.t;

        const title =
          t?.performanceTitle ??
          t?.eventTitle ??
          t?.performance?.title ??
          t?.performance?.event?.title ??
          t?.event?.title ??
          t?.title ??
          'Ticket';

        const line: ReservationTicketLine = {
          ticketId: x.tid,
          title,
          seatLabel: this.ticketSeatLabel(t),
          priceCents: t?.priceFinalCents ?? 0
        };

        (this.reservationLinesById[x.rid] ||= []).push(line);

        this.reservationSelectedById[x.rid] ||= {};
        if (this.reservationSelectedById[x.rid][x.tid] === undefined) {
          this.reservationSelectedById[x.rid][x.tid] = true;
        }
      }

      for (const ridStr of Object.keys(this.reservationLinesById)) {
        const rid = Number(ridStr);
        const sum = (this.reservationLinesById[rid] ?? []).reduce((s, l) => s + (l.priceCents ?? 0), 0);
        this.reservationTotalCentsById[rid] = sum;
      }
    });
  }


  private cacheKey(orderId: number): string {
    return `order-meta-${orderId}`;
  }

  private saveOrderMeta(orderId: number, eventTitle: string, seats: string[]) {
    const payload: OrderMetaCache = {
      eventTitle: eventTitle || 'Unbekannte Veranstaltung',
      seats: seats ?? [],
      savedAt: new Date().toISOString()
    };
    try {
      localStorage.setItem(this.cacheKey(orderId), JSON.stringify(payload));
    } catch {
    }
  }

  private loadOrderMeta(orderId: number): OrderMetaCache | null {
    try {
      const raw = localStorage.getItem(this.cacheKey(orderId));
      if (!raw) return null;
      return JSON.parse(raw) as OrderMetaCache;
    } catch {
      return null;
    }
  }

  private applyCachedMeta(orders: OrderDto[]) {
    for (const o of orders) {
      if (!this.isCancelled(o)) continue;

      const meta = this.loadOrderMeta(o.id);
      if (!meta) continue;

      if (!this.eventTitleByOrderId[o.id]) {
        this.eventTitleByOrderId[o.id] = meta.eventTitle;
      }

      const seatsNow = this.seatsByOrderId[o.id];
      if (!seatsNow || seatsNow.length === 0) {
        this.seatsByOrderId[o.id] = meta.seats;
      }
    }
  }

  private cleanupMetaForActiveOrders(orders: OrderDto[]) {
    for (const o of orders) {
      if (this.isCancelled(o)) continue;
      try {
        localStorage.removeItem(this.cacheKey(o.id));
      } catch {
      }
    }
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
          map(t => ({ orderId: p.orderId, performanceId: (t as any)?.performanceId })),
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((x): x is { orderId: number; performanceId: number } => x !== null && x.performanceId != null))
    ).subscribe(orderPerfList => {
      const perfIds = Array.from(new Set(orderPerfList.map(x => x.performanceId)));
      if (perfIds.length === 0) return;

      forkJoin(
        perfIds.map(id =>
          this.performancesService.getById(id).pipe(
            map(p => ({
              perfId: id,
              eventId: (p as any)?.eventId ?? (p as any)?.event?.id ?? null,
              title:
                (p as any)?.title ??
                (p as any)?.eventTitle ??
                (p as any)?.event?.title ??
                (p as any)?.event?.name ??
                (p as any)?.name ??
                null
            })),
            catchError(() => of({ perfId: id, eventId: null, title: null }))
          )
        )
      ).subscribe(perfs => {
        const titleByPerf = new Map<number, string>();
        const perfToEvent = new Map<number, number>();

        for (const p of perfs) {
          if (p.eventId != null) perfToEvent.set(p.perfId, p.eventId);
          if (p.title) titleByPerf.set(p.perfId, p.title);
        }

        const missingPerfIds = perfIds.filter(pid => !titleByPerf.has(pid) && perfToEvent.has(pid));
        const eventIds = Array.from(new Set(missingPerfIds.map(pid => perfToEvent.get(pid)!).filter(Boolean)));

        const buildOrderTitles = () => {
          const byOrder: Record<number, string[]> = {};
          for (const x of orderPerfList) {
            const title = titleByPerf.get(x.performanceId) ?? 'Unbekannte Veranstaltung';
            (byOrder[x.orderId] ||= []).push(title);
          }
          for (const [oid, titles] of Object.entries(byOrder)) {
            this.eventTitleByOrderId[Number(oid)] = Array.from(new Set(titles)).join(' • ');
          }
        };

        if (eventIds.length === 0) {
          buildOrderTitles();
          return;
        }

        forkJoin(
          eventIds.map(eid =>
            this.eventsService.getEventById(eid).pipe(
              map(e => ({ id: eid, title: (e as any)?.title ?? (e as any)?.name ?? null })),
              catchError(() => of({ id: eid, title: null }))
            )
          )
        ).subscribe(events => {
          const titleByEvent = new Map<number, string>();
          for (const e of events) {
            if (e.title) titleByEvent.set(e.id, e.title);
          }

          for (const pid of missingPerfIds) {
            const eid = perfToEvent.get(pid);
            const t = eid != null ? titleByEvent.get(eid) : null;
            if (t) titleByPerf.set(pid, t);
          }

          buildOrderTitles();
        });
      });
    });
  }

  private loadSeatsForOrders(orders: OrderDto[]) {
    this.seatsByOrderId = {};
    this.ticketCountByOrderId = {};

    const pairs: { orderId: number; ticketId: number }[] = [];

    for (const o of orders) {
      const ids = (o.ticketIds ?? []);
      this.ticketCountByOrderId[o.id] = ids.length;

      for (const tid of ids) {
        pairs.push({ orderId: o.id, ticketId: tid });
      }
    }

    if (pairs.length === 0) return;

    forkJoin(
      pairs.map(p =>
        this.ticketsService.getTicketById(p.ticketId).pipe(
          map(t => ({ orderId: p.orderId, ticket: t })),
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((x): x is { orderId: number; ticket: any } => x !== null))
    ).subscribe(list => {
      for (const x of list) {
        const seat = this.ticketSeatLabel(x.ticket);
        const arr = (this.seatsByOrderId[x.orderId] ||= []);
        if (!arr.includes(seat)) arr.push(seat);
      }
    });
  }


  private loadEventTitlesForReservations(reservations: ReservationDto[]) {
    this.eventTitleByReservationId = {};

    const pairs: { rid: number; ticketId: number }[] = [];
    for (const r of reservations) {
      for (const tid of (r.ticketIds ?? [])) {
        pairs.push({ rid: r.id, ticketId: tid });
      }
    }
    if (pairs.length === 0) return;

    forkJoin(
      pairs.map(p =>
        this.ticketsService.getTicketById(p.ticketId).pipe(
          map(t => ({ rid: p.rid, performanceId: (t as any)?.performanceId ?? (t as any)?.performance?.id ?? null })),
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((x): x is { rid: number; performanceId: number } => x !== null && x.performanceId != null))
    ).subscribe(resPerfList => {
      const perfIds = Array.from(new Set(resPerfList.map(x => x.performanceId)));
      if (perfIds.length === 0) return;

      forkJoin(
        perfIds.map(id =>
          this.performancesService.getById(id).pipe(
            map(p => ({
              perfId: id,
              eventId: (p as any)?.eventId ?? (p as any)?.event?.id ?? null,
              title:
                (p as any)?.title ??
                (p as any)?.eventTitle ??
                (p as any)?.event?.title ??
                (p as any)?.event?.name ??
                (p as any)?.name ??
                null
            })),
            catchError(() => of({ perfId: id, eventId: null, title: null }))
          )
        )
      ).subscribe(perfs => {
        const titleByPerf = new Map<number, string>();
        const perfToEvent = new Map<number, number>();

        for (const p of perfs) {
          if (p.eventId != null) perfToEvent.set(p.perfId, p.eventId);
          if (p.title) titleByPerf.set(p.perfId, p.title);
        }

        const missingPerfIds = perfIds.filter(pid => !titleByPerf.has(pid) && perfToEvent.has(pid));
        const eventIds = Array.from(new Set(missingPerfIds.map(pid => perfToEvent.get(pid)!).filter(Boolean)));

        const buildReservationTitles = () => {
          const byRes: Record<number, string[]> = {};
          for (const x of resPerfList) {
            const title = titleByPerf.get(x.performanceId) ?? 'Unbekannte Veranstaltung';
            (byRes[x.rid] ||= []).push(title);
          }
          for (const [rid, titles] of Object.entries(byRes)) {
            this.eventTitleByReservationId[Number(rid)] = Array.from(new Set(titles)).join(' • ');
          }
        };

        if (eventIds.length === 0) {
          buildReservationTitles();
          return;
        }

        forkJoin(
          eventIds.map(eid =>
            this.eventsService.getEventById(eid).pipe(
              map(e => ({ id: eid, title: (e as any)?.title ?? (e as any)?.name ?? null })),
              catchError(() => of({ id: eid, title: null }))
            )
          )
        ).subscribe(events => {
          const titleByEvent = new Map<number, string>();
          for (const e of events) {
            if (e.title) titleByEvent.set(e.id, e.title);
          }

          for (const pid of missingPerfIds) {
            const eid = perfToEvent.get(pid);
            const t = eid != null ? titleByEvent.get(eid) : null;
            if (t) titleByPerf.set(pid, t);
          }

          buildReservationTitles();
        });
      });
    });
  }

  private loadSeatsForReservations(reservations: ReservationDto[]) {
    this.reservationSeatsById = {};
    this.reservationTicketCountById = {};

    const pairs: { rid: number; ticketId: number }[] = [];
    for (const r of reservations) {
      for (const tid of (r.ticketIds ?? [])) {
        pairs.push({ rid: r.id, ticketId: tid });
      }
      this.reservationTicketCountById[r.id] = (r.ticketIds ?? []).length;
    }
    if (pairs.length === 0) return;

    forkJoin(
      pairs.map(p =>
        this.ticketsService.getTicketById(p.ticketId).pipe(
          map(t => ({ rid: p.rid, ticket: t })),
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((x): x is { rid: number; ticket: any } => x !== null))
    ).subscribe(list => {
      for (const x of list) {
        const seat = this.ticketSeatLabel(x.ticket);
        const arr = (this.reservationSeatsById[x.rid] ||= []);
        if (!arr.includes(seat)) arr.push(seat);
      }
    });
  }
}
