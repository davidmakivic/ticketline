import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';

import {forkJoin, Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';

import {CartService} from '../../services/cart.service';
import {TicketsService} from '../../services/tickets.service';
import {Ticket} from '../../dtos/ticket';
import {TicketCartItemComponent} from '../tickets/ticket-cart-item/ticket-cart-item.component';
import {AuthService} from '../../services/auth.service';

import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {MerchandiseService} from '../../services/merchandise.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    TicketCartItemComponent
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent {
  loading = false;
  tickets: Ticket[] = [];
  merchItems: any[] = [];
  rewardItems: any[] = [];
  private merchImages = new Map<number, SafeUrl>();

  constructor(
    private cart: CartService,
    private ticketsService: TicketsService,
    private authService: AuthService,
    private router: Router,
    private merchService: MerchandiseService,
    private sanitizer: DomSanitizer
  ) {
    this.cart.cartItems$.subscribe((items: any[]) => this.syncFromCart(items ?? []));
  }

  clear(): void {
    const ids = (this.cart.getCartItems() ?? [])
      .map((i: any) => Number(i?.ticketId))
      .filter((n: number) => Number.isFinite(n) && n > 0);

    this.loading = true;

    const release$ = ids.length
      ? forkJoin(
        ids.map(id =>
          this.cart.removeTicketAndRelease(id).pipe(
            catchError(err => {
              console.error('Release failed for ticket', id, err);
              return of(null);
            })
          )
        )
      )
      : of([]);

    release$.subscribe({
      next: () => {
        this.loading = false;
        this.cart.clear();
        this.tickets = [];
        this.merchItems = [];
        this.merchImages.clear();
      },
      error: (e) => {
        console.error(e);
        this.loading = false;
        this.cart.clear();
        this.tickets = [];
        this.merchItems = [];
        this.merchImages.clear();
      }
    });
  }


  removeTicket(ticket: Ticket): void {
    this.cart.removeTicketAndRelease(ticket.id).subscribe({
      next: () => {
      },
      error: (e) => console.error(e)
    });
  }

  removeMerch(m: any): void {
    const service: any = this.cart as any;

    const variantId = Number(m?.variantId ?? m?.merchVariantId ?? m?.id);
    const merchId = Number(m?.merchandiseId ?? m?.merchId);

    const callAndReload = (result: any) => {
      if (result?.subscribe) {
        result.subscribe({
          next: () => this.reload(),
          error: (e: any) => {
            console.error(e);
            this.reload();
          }
        });
      } else {
        this.reload();
      }
    };

    if (service.removeMerchByVariantId && Number.isFinite(variantId)) {
      callAndReload(service.removeMerchByVariantId(variantId));
      return;
    }

    if (service.removeMerch && Number.isFinite(variantId)) {
      callAndReload(service.removeMerch(variantId));
      return;
    }

    if (service.removeMerchByMerchandiseId && Number.isFinite(merchId)) {
      callAndReload(service.removeMerchByMerchandiseId(merchId));
      return;
    }

    this.reload();
  }

  get totalPriceCents(): number {
    const ticketTotal = (this.tickets ?? []).reduce((sum, t) => sum + (t.priceFinalCents ?? 0), 0);

    const merchTotal = (this.merchItems ?? []).reduce((sum, m) => {
      const qty = Number(m?.quantity ?? 1);
      const unit = Number(m?.unitPriceCents ?? m?.priceCents ?? m?.price ?? 0);
      const line = Number.isFinite(Number(m?.lineTotalCents)) ? Number(m.lineTotalCents) : (unit * qty);
      return sum + (Number.isFinite(line) ? line : 0);
    }, 0);

    return ticketTotal + merchTotal;
  }

  get hasAnyItems(): boolean {
    return (this.tickets?.length ?? 0) > 0 || (this.merchItems?.length ?? 0) > 0 || (this.rewardItems?.length ?? 0) > 0;
  }

  onBuy(): void {
    if (!this.hasAnyItems) return;

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], {queryParams: {redirect: '/checkout'}});
      return;
    }

    this.router.navigate(['/checkout']);
  }

  merchImage(id: number): SafeUrl | null {
    return this.merchImages.get(Number(id)) ?? null;
  }

  private reload(): void {
    this.syncFromCart(this.cart.getCartItems() ?? []);
  }

  private syncFromCart(items: any[]): void {
    const ticketIds = (items ?? [])
      .map(i => Number(i?.ticketId))
      .filter(n => Number.isFinite(n) && n > 0);

    this.merchItems = (items ?? []).filter(i =>
      i &&
      !(Number.isFinite(Number(i?.ticketId)) && Number(i.ticketId) > 0) &&
      (i?.variantId != null || i?.merchandiseId != null) && i.kind === 'merch'
    );

    this.rewardItems = (items ?? []).filter(i =>
      i &&
      !(Number.isFinite(Number(i?.ticketId)) && Number(i.ticketId) > 0) &&
      (i?.variantId != null || i?.merchandiseId != null) && i.kind === 'reward'
    );

    this.loadTickets(ticketIds);
    this.loadMerchImages([...this.merchItems, ...this.rewardItems]);
  }

  private loadTickets(ids: number[]): void {
    if (!ids.length) {
      this.tickets = [];
      this.loading = false;
      return;
    }

    this.loading = true;

    forkJoin(
      ids.map(id =>
        this.ticketsService.getTicketById(id).pipe(
          catchError(() => of(null))
        )
      )
    ).pipe(
      map(list => list.filter((t): t is Ticket => t !== null))
    ).subscribe({
      next: (tickets) => {
        const order = new Map(ids.map((id, idx) => [id, idx]));
        this.tickets = tickets.sort((a, b) => (order.get(a.id) ?? 0) - (order.get(b.id) ?? 0));
        this.loading = false;
      },
      error: () => {
        this.tickets = [];
        this.loading = false;
      }
    });
  }

  private loadMerchImages(items: any[]): void {
    const ids = Array.from(new Set(
      (items ?? [])
        .map(i => Number(i?.merchandiseId))
        .filter(n => Number.isFinite(n) && n > 0)
    ));

    ids.forEach(id => {
      if (this.merchImages.has(id)) return;

      this.merchService.getImage(id).subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);
          this.merchImages.set(id, this.sanitizer.bypassSecurityTrustUrl(url));
        },
        error: () => {
        }
      });
    });
  }

  merchQty(m: any): number {
    const q = Number(m?.quantity ?? 1);
    return Number.isFinite(q) && q > 0 ? Math.floor(q) : 1;
  }

  incMerchQty(m: any): void {
    this.setMerchQty(m, this.merchQty(m) + 1);
  }

  decMerchQty(m: any): void {
    this.setMerchQty(m, this.merchQty(m) - 1);
  }

  private setMerchQty(m: any, qty: number): void {
    const newQty = Math.max(1, Math.floor(Number(qty) || 1));
    if (m.kind === 'merch') {
      this.cart.updateMerchQuantity(m.variantId, newQty);
      return;
    }
    if (m.kind === 'reward') {
      this.cart.updateRewardQuantity(m.variantId, newQty);
      return;
    }

    (this as any).cart?.persist?.();
  }

  canIncreaseReward(r: any): Observable<boolean> {
    return this.cart.availableRewardPoints$.pipe(
      map(points => points >= r.unitPricePoints)
    );
  }

  getUsedRewardPoints(): number {
    return this.cart.usedRewardPoints();
  }

  protected removeReward(r: any) {
    if (!r || r.kind !== 'reward') return;
    this.cart.removeReward(r.variantId);
  }
}
