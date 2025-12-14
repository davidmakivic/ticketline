import { Component, ElementRef, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { forkJoin, switchMap } from 'rxjs';

import { PerformancesService } from '../../services/performances.service';
import { HallsService } from '../../services/halls.service';
import { VenuesService } from '../../services/venues.service';
import { SeatsService } from '../../services/seats.service';
import { TicketsService } from '../../services/tickets.service';
import { CartService } from '../../services/cart.service';
import { Router } from '@angular/router';


import { Performance } from '../../dtos/performance';
import { Hall, LayoutElement, LayoutMetadata, SectorIndexEntry } from '../../dtos/hall';
import { Venue } from '../../dtos/venue';
import { Seat } from '../../dtos/seat';
import { Ticket, TicketStatus } from '../../dtos/ticket';

import { EventsService   } from "../../services/events.service";
import { EventDto } from "../../dtos/event";

type StageEl = Extract<LayoutElement, { type: 'stage' }>;
type StandingEl = Extract<LayoutElement, { type: 'standingArea' }>;
type SeatBlockEl = Extract<LayoutElement, { type: 'seatBlock' }>;

type SeatUiStatus = 'free' | 'reserved' | 'selected' | 'missing';

@Component({
  selector: 'app-seat-selection',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seat-selection.component.html',
  styleUrls: ['./seat-selection.component.scss']
})
export class SeatSelectionComponent implements AfterViewInit, OnDestroy {
  loading = true;
  error: string | null = null;

  performanceId!: number;

  performance!: Performance;
  hall!: Hall;
  venue!: Venue;
  event?: EventDto;

  layout!: LayoutMetadata;

  // seatId -> sectorKey (für sectorSummary)
  sectorKeyBySeatId = new Map<number, string>();

  // sectorKey -> alle seatIds in dem sector
  seatIdsBySectorKey = new Map<string, number[]>();

  // container size for responsive svg
  @ViewChild('planWrap', { static: false }) planWrap?: ElementRef<HTMLDivElement>;
  cw = 800;
  ch = 700;
  private ro?: ResizeObserver;

  // Lookups
  sectorByKey = new Map<string, SectorIndexEntry>();      // sectorKey -> sectorIndexEntry
  sectorKeyBySectorId = new Map<number, string>();        // sectorId -> sectorKey

  // Build key: `${sectorKey}|${row}|${seat}` -> seatId
  seatIdByKey = new Map<string, number>();

  // seatId -> ticket
  ticketBySeatId = new Map<number, Ticket>();

  // selected seatIds
  selectedSeatIds = new Set<number>();

  constructor(
    private route: ActivatedRoute,
    private performancesService: PerformancesService,
    private hallsService: HallsService,
    private venuesService: VenuesService,
    private seatsService: SeatsService,
    private ticketsService: TicketsService,
     private cart: CartService,
      private router: Router,
    private eventsService: EventsService
  ) {
    this.init();
  }

  ngAfterViewInit(): void {
    if (!this.planWrap) return;
    this.ro = new ResizeObserver(() => this.syncContainerSize());
    this.ro.observe(this.planWrap.nativeElement);
    this.syncContainerSize();
  }

  ngOnDestroy(): void {
    if (this.ro && this.planWrap) {
      this.ro.disconnect();
    }
  }

  private syncContainerSize() {
    if (!this.planWrap) return;
    const rect = this.planWrap.nativeElement.getBoundingClientRect();
    this.cw = Math.max(320, rect.width);
    this.ch = Math.max(260, rect.height);
  }

  private init() {
    this.loading = true;
    this.error = null;

    this.route.paramMap.pipe(
      switchMap(params => {
        this.performanceId = Number(params.get('performanceId'));
        return this.performancesService.getById(this.performanceId);
      }),
      switchMap(perf => {
        this.performance = perf;
        return forkJoin({
          hall: this.hallsService.getById(perf.hallId),
          tickets: this.ticketsService.getTicketsByPerformance(perf.id),
          event: this.eventsService.getEventById(perf.eventId)
        });
      }),
      switchMap(({ hall, tickets, event }) => {
        this.hall = hall;
        this.layout = hall.layoutMetadata ?? { elements: [], version: 1 };
        this.event = event;
        // sector index mappings
        this.sectorByKey.clear();
        this.sectorKeyBySectorId.clear();
        for (const s of hall.sectorIndex ?? []) {
          this.sectorByKey.set(s.sectorKey, s);
          this.sectorKeyBySectorId.set(s.id, s.sectorKey);
        }

        // tickets by seatId
        this.ticketBySeatId.clear();
        for (const t of tickets) {
          if (t.seatId == null) continue;
          this.ticketBySeatId.set(t.seatId, t);
        }

        // Load venue + seats for sectors used in this hall
        const seatCalls = (hall.sectorIndex ?? []).map(s => this.seatsService.getBySectorId(s.id));

        return forkJoin({
          venue: this.venuesService.getById(hall.venueId),
          seatsBySector: seatCalls.length ? forkJoin(seatCalls) : forkJoin([])
        });
      })
    ).subscribe({
      next: ({ venue, seatsBySector }) => {
        this.venue = venue;

        const allSeats: Seat[] = [];
        for (const arr of seatsBySector as any[]) {
          for (const seat of (arr as Seat[])) allSeats.push(seat);
        }

        // build seat mappings
        this.seatIdByKey.clear();
        this.sectorKeyBySeatId.clear();
        this.seatIdsBySectorKey.clear();

        for (const seat of allSeats) {
          const sectorKey = this.sectorKeyBySectorId.get(seat.sectorId);
          if (!sectorKey) continue;

          this.seatIdByKey.set(this.seatKey(sectorKey, seat.rowNumber, seat.seatNumber), seat.id);
          this.sectorKeyBySeatId.set(seat.id, sectorKey);

          const arr = this.seatIdsBySectorKey.get(sectorKey) ?? [];
          arr.push(seat.id);
          this.seatIdsBySectorKey.set(sectorKey, arr);
        }

        // Optional: wenn bisher selektierte seats plötzlich nicht mehr existieren (z.B. reload),
        // bereinigen wir:
        for (const seatId of Array.from(this.selectedSeatIds)) {
          if (!this.ticketBySeatId.has(seatId)) this.selectedSeatIds.delete(seatId);
        }

        this.loading = false;
      },
      error: (e) => {
        console.error(e);
        this.error = 'Konnte Sitzplan nicht laden.';
        this.loading = false;
      }
    });
  }


addSelectedToCart() {
  const calls = [];

  for (const seatId of this.selectedSeatIds) {
    const ticket = this.ticketBySeatId.get(seatId);
    if (!ticket) continue;

    calls.push(this.cart.addTicketAndReserve(ticket.id));
  }

  if (calls.length === 0) {
    this.clearSelection();
    this.router.navigate(['/cart']);
    return;
  }

  this.loading = true;
  this.error = null;

  forkJoin(calls).subscribe({
    next: () => {
      this.loading = false;
      this.clearSelection();
      this.router.navigate(['/cart']);
    },
    error: (e) => {
      console.error(e);
      this.loading = false;
      this.error = 'Reservierung fehlgeschlagen (Ticket evtl. nicht mehr verfügbar).';
    }
  });
}



  // ---- aspect ratio lock box ----
  get box() {
    const aw = this.layout?.aspect?.w ?? 3;
    const ah = this.layout?.aspect?.h ?? 2;

    let targetW = this.cw;
    let targetH = this.cw * (ah / aw);

    if (targetH > this.ch) {
      targetH = this.ch;
      targetW = this.ch * (aw / ah);
    }

    const offsetX = (this.cw - targetW) / 2;
    const offsetY = (this.ch - targetH) / 2;

    return { targetW, targetH, offsetX, offsetY };
  }

  x(n: number) {
    const b = this.box; return b.offsetX + n * b.targetW;
  }
  y(n: number) {
    const b = this.box; return b.offsetY + n * b.targetH;
  }
  w(n: number) {
    return n * this.box.targetW;
  }
  h(n: number) {
    return n * this.box.targetH;
  }
  dx(n: number) {
    return n * this.box.targetW;
  }
  dy(n: number) {
    return n * this.box.targetH;
  }

  get seatR(): number {
    const b = this.box;
    return Math.min(b.targetW, b.targetH) * 0.025;
  }

  // ---- layout helpers ----
  elements(): LayoutElement[] {
    return this.layout?.elements ?? [];
  }

  isStage(el: LayoutElement): el is StageEl {
    return el.type === 'stage';
  }
  isStanding(el: LayoutElement): el is StandingEl {
    return el.type === 'standingArea';
  }
  isSeatBlock(el: LayoutElement): el is SeatBlockEl {
    return el.type === 'seatBlock';
  }

  // ---- seat state mapping ----
  seatKey(sectorKey: string, row: number, seat: number): string {
    return `${sectorKey}|${row}|${seat}`;
  }

  seatStatus(sectorKey: string, row: number, seat: number): SeatUiStatus {
    const seatId = this.seatIdByKey.get(this.seatKey(sectorKey, row, seat));
    if (!seatId) return 'missing';

    const ticket = this.ticketBySeatId.get(seatId);
    if (!ticket) return 'missing';

    if (ticket.status !== TicketStatus.AVAILABLE) return 'reserved';
    if (this.selectedSeatIds.has(seatId)) return 'selected';
    return 'free';
  }

  onSeatClick(sectorKey: string, row: number, seat: number) {
    const seatId = this.seatIdByKey.get(this.seatKey(sectorKey, row, seat));
    if (!seatId) return;

    const ticket = this.ticketBySeatId.get(seatId);
    if (!ticket || ticket.status !== TicketStatus.AVAILABLE) return;

    if (this.selectedSeatIds.has(seatId)) this.selectedSeatIds.delete(seatId);
    else this.selectedSeatIds.add(seatId);
  }

  // ---- standing seat state mapping

  standingAvailableCount(sectorKey: string): number {
    const seatIds = this.seatIdsBySectorKey.get(sectorKey) ?? [];
    let c = 0;

    for (const seatId of seatIds) {
      const t = this.ticketBySeatId.get(seatId);
      if (t && t.status === TicketStatus.AVAILABLE && !this.selectedSeatIds.has(seatId)) c++;
    }
    return c;
  }

  standingSelectedCount(sectorKey: string): number {
    const seatIds = this.seatIdsBySectorKey.get(sectorKey) ?? [];
    let c = 0;
    for (const seatId of seatIds) {
      if (this.selectedSeatIds.has(seatId)) c++;
    }
    return c;
  }

  standingStatus(sectorKey: string): 'free' | 'reserved' | 'selected' {
    if (this.standingSelectedCount(sectorKey) > 0) return 'selected';
    return this.standingAvailableCount(sectorKey) > 0 ? 'free' : 'reserved';
  }

  standingAvailabilityLabel(sectorKey: string): string {
    const n = this.standingAvailableCount(sectorKey);
    return n > 0 ? `${n} verfügbar` : 'Ausverkauft';
  }

  private selectOneStanding(sectorKey: string) {
    const seatIds = this.seatIdsBySectorKey.get(sectorKey) ?? [];

    // pick first AVAILABLE + not already selected
    for (const seatId of seatIds) {
      const t = this.ticketBySeatId.get(seatId);
      if (!t) continue;
      if (t.status !== TicketStatus.AVAILABLE) continue;
      if (this.selectedSeatIds.has(seatId)) continue;

      this.selectedSeatIds.add(seatId);
      return;
    }
  }

  private deselectOneStanding(sectorKey: string) {
    const seatIds = this.seatIdsBySectorKey.get(sectorKey) ?? [];

    for (let i = seatIds.length - 1; i >= 0; i--) {
      const seatId = seatIds[i];
      if (this.selectedSeatIds.has(seatId)) {
        this.selectedSeatIds.delete(seatId);
        return;
      }
    }
  }

  onStandingClick(ev: PointerEvent, sectorKey: string) {
    // Right click OR Shift-click => remove one
    const remove = ev.button === 2 || ev.shiftKey;

    // Prevent text selection / context menu quirks
    ev.preventDefault();

    if (remove) {
      this.deselectOneStanding(sectorKey);
    } else {
      this.selectOneStanding(sectorKey);
    }
  }

  // ---- header formatting ----
  private formatDate(iso?: string): string {
    if (!iso) return '';
    return new Intl.DateTimeFormat('de-AT', { day: '2-digit', month: 'long', year: 'numeric' }).format(new Date(iso));
  }

  private formatTime(iso?: string): string {
    if (!iso) return '';
    return new Intl.DateTimeFormat('de-AT', { hour: '2-digit', minute: '2-digit' }).format(new Date(iso));
  }

  get dateLabel(): string {
    return this.formatDate(this.performance?.startTime);
  }
  get city(): string {
    return this.venue?.city ?? '';
  }

  get title(): string {
    return this.event?.title ?? '';
  }

  get doorsOpenLabel(): string {
    const d = new Date(this.performance.startTime);
    d.setMinutes(d.getMinutes() - 30);
    return `Einlass ${this.formatTime(d.toISOString())} Uhr`;
  }

  get beginLabel(): string {
    return `Beginn ${this.formatTime(this.performance?.startTime)} Uhr`;
  }
  get endLabel(): string {
    return `Ende ${this.formatTime(this.performance?.endTime)} Uhr`;
  }
  get venueLine(): string {
    return `${this.venue?.name ?? ''} – ${this.hall?.name ?? ''}`;
  }

  get addressLine(): string {
    const v = this.venue;
    return v ? `${v.street}, ${v.postalCode} ${v.city}` : '';
  }

  euro(cents: number): string {
    return new Intl.NumberFormat('de-AT', { style: 'currency', currency: 'EUR' }).format((cents ?? 0) / 100);
  }

  get totalCents(): number {
    let sum = 0;
    for (const seatId of this.selectedSeatIds) {
      const t = this.ticketBySeatId.get(seatId);
      if (t) sum += (t.priceFinalCents ?? 0);
    }
    return sum;
  }

  clearSelection() {
    this.selectedSeatIds.clear();
  }

  range(n: number): number[] {
    return Array.from({ length: Math.max(0, n) }, (_, i) => i);
  }

  // ---- summary: selected tickets grouped by sectorKey ----
  sectorSummary(): Array<{ sectorKey: string; name: string; count: number; priceLabel: string }> {
    const bySector = new Map<string, number[]>();

    for (const seatId of this.selectedSeatIds) {
      const ticket = this.ticketBySeatId.get(seatId);
      if (!ticket) continue;

      const sectorKey = this.sectorKeyBySeatId.get(seatId);
      if (!sectorKey) continue;

      const arr = bySector.get(sectorKey) ?? [];
      arr.push(ticket.priceFinalCents ?? 0);
      bySector.set(sectorKey, arr);
    }

    const result: Array<{ sectorKey: string; name: string; count: number; priceLabel: string }> = [];

    for (const [sectorKey, prices] of bySector.entries()) {
      const unique = Array.from(new Set(prices)).sort((a, b) => a - b);
      const priceLabel =
        unique.length <= 1
          ? `Preis pro Ticket: ${this.euro(unique[0] ?? 0)}`
          : `Preis pro Ticket: ${this.euro(unique[0])} – ${this.euro(unique[unique.length - 1])}`;

      const name = this.sectorByKey.get(sectorKey)?.name ?? `Sektor ${sectorKey}`;

      result.push({ sectorKey, name, count: prices.length, priceLabel });
    }

    result.sort((a, b) => Number(a.sectorKey) - Number(b.sectorKey));
    return result;
  }

  // ---- reserve selection (sets tickets to RESERVED) ----
  reserveSelected() {
    const toReserve: Ticket[] = [];

    for (const seatId of this.selectedSeatIds) {
      const t = this.ticketBySeatId.get(seatId);
      if (!t) continue;
      if (t.status !== TicketStatus.AVAILABLE) continue;
      toReserve.push(t);
    }

    if (toReserve.length === 0) return;

    this.loading = true;
    this.error = null;

    forkJoin(
      toReserve.map(t =>
        this.ticketsService.updateStatus(t.id, { status: TicketStatus.RESERVED, version: t.version })
      )
    ).subscribe({
      next: (updatedTickets) => {
        // update local state
        for (const t of updatedTickets) {
          if (t.seatId == null) continue;
          this.ticketBySeatId.set(t.seatId, t);
        }
        this.clearSelection();
        this.loading = false;
      },
      error: (e) => {
        console.error(e);
        this.error = 'Reservierung fehlgeschlagen (evtl. wurde ein Platz gerade vergeben).';
        this.loading = false;
      }
    });
  }
}
