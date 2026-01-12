import { Component, Renderer2, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketsService } from '../../services/tickets.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

type CancellationResultDto = {
  orderId: number;
  cancelledTicketIds: number[];
  refundTotalCents: number;
  createdAt: string;
};

type State = {
  cancellation?: CancellationResultDto;
  customerName?: string;
  payment?: string;
  originalInvoiceNo?: string;
  eventTitle?: string;
  seats?: string[];
};

@Component({
  selector: 'app-storno-invoice',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './storno-invoice.component.html',
  styleUrls: ['./storno-invoice.component.scss']
})
export class StornoInvoiceComponent implements OnDestroy {

  cancellation?: CancellationResultDto;

  stornoNo = '';
  stornoDateStr = '';
  customerName = 'Kunde';
  originalInvoiceNo = '';
  tickets: any[] = [];

  eventTitle = '';
  seatLabels: string[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private renderer: Renderer2,
    private ticketsService: TicketsService
  ) {
    const idFromRoute = Number(this.route.snapshot.paramMap.get('id'));

    const state = history.state as State;
    const cached = this.loadCancellation(idFromRoute);

    const effective: State = (state && state.cancellation) ? state : (cached ?? {});

    this.cancellation = effective.cancellation;

    this.eventTitle = effective.eventTitle ?? '';
    this.seatLabels = effective.seats ?? [];

    const ids = this.cancellation?.cancelledTicketIds ?? [];

    if (ids.length) {
      forkJoin(
        ids.map(id =>
          this.ticketsService.getTicketById(id).pipe(
            catchError(() => of(null))
          )
        )
      ).pipe(
        map(list => list.filter(x => x != null))
      ).subscribe(list => this.tickets = list);
    }

    this.customerName = effective.customerName ?? 'Kunde';
    this.originalInvoiceNo = effective.originalInvoiceNo ?? '';

    const dt = this.cancellation?.createdAt ? new Date(this.cancellation.createdAt) : new Date();
    this.stornoNo = this.buildStornoNumber(this.cancellation?.orderId ?? idFromRoute ?? 0, dt);
    this.stornoDateStr = this.formatDate(dt);

    this.renderer.addClass(document.body, 'invoice-print');

    const shouldPrint = this.route.snapshot.queryParamMap.get('print') === '1';
    if (shouldPrint) {
      setTimeout(() => window.print(), 200);
    }
  }

  ngOnDestroy(): void {
    this.renderer.removeClass(document.body, 'invoice-print');
  }

  print(): void {
    window.print();
  }

  back(): void {
    this.router.navigate(['/']);
  }

  refundEuro(): string {
    const cents = this.cancellation?.refundTotalCents ?? 0;
    return '-' + this.toEuro(Math.abs(cents));
  }

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }

  private cancellationKey(orderId: number) {
    return `order-cancel-${orderId}`;
  }

  private loadCancellation(orderId: number): State | null {
    try {
      const raw = localStorage.getItem(this.cancellationKey(orderId));
      return raw ? (JSON.parse(raw) as State) : null;
    } catch {
      return null;
    }
  }

  private buildStornoNumber(orderId: number, date: Date): string {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `ST-${yyyy}${mm}${dd}-${String(orderId).padStart(5, '0')}`;
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

    const seat = (row != null && number != null)
      ? `Reihe ${row}, Sitz ${number}`
      : (t?.seatId != null ? `Sitz #${t.seatId}` : null);

    if (sector && seat) return `${sector} • ${seat}`;
    if (seat) return seat;
    return 'Freie Platzwahl';
  }
}
