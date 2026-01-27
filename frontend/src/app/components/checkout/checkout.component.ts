import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';

import {CartService} from '../../services/cart.service';
import {OrdersService} from '../../services/order.service';
import {TicketsService} from '../../services/tickets.service';
import {Ticket} from '../../dtos/ticket';

import {forkJoin, of} from 'rxjs';
import {catchError, map, switchMap, tap} from 'rxjs/operators';
import {MatSnackBar} from "@angular/material/snack-bar";

import {ReservationsService} from '../../services/reservations.service';
import {CartItem, isMerchItem, isRewardItem, isTicketItem} from '../../dtos/cart-item';
import {RewardService} from "../../services/reward.service";

type PaymentId = 'card' | 'paypal' | 'klarna' | 'applepay';

interface PaymentMethod {
  id: PaymentId;
  label: string;
  logo: string;
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent {

  selectedPayment: PaymentId = 'card';

  paymentMethods: PaymentMethod[] = [
    {id: 'card', label: 'Kreditkarte', logo: '/assets/payments/visa-mastercard.svg'},
    {id: 'paypal', label: 'PayPal', logo: '/assets/payments/paypal.svg'},
    {id: 'klarna', label: 'Klarna', logo: '/assets/payments/klarna.svg'},
    {id: 'applepay', label: 'Apple Pay', logo: '/assets/payments/apple-pay.svg'}
  ];

  readonly totalCents$ = this.cart.cartItems$.pipe(
    switchMap(items => {
      const ticketIds = items.filter(isTicketItem).map(i => i.ticketId);
      const merchSum = items
        .filter(isMerchItem)
        .reduce((s, m) => s + (m.unitPriceCents ?? 0) * (m.quantity ?? 0), 0);

      if (ticketIds.length === 0) return of(merchSum);

      return forkJoin(
        ticketIds.map(id =>
          this.ticketsService.getTicketById(id).pipe(catchError(() => of(null)))
        )
      ).pipe(
        map(list => list.filter((t): t is Ticket => t !== null)),
        map(tickets => merchSum + tickets.reduce((sum, t) => sum + (t.priceFinalCents ?? 0), 0))
      );
    })
  );

  constructor(
    public cart: CartService,
    private ticketsService: TicketsService,
    private orders: OrdersService,
    private router: Router,
    private reservationsService: ReservationsService,
    private snackBar: MatSnackBar,
    private rewardService: RewardService
  ) {
  }

  select(method: PaymentId): void {
    this.selectedPayment = method;
  }

  pay(): void {
    const items = this.cart.getCartItems();
    if (items.length === 0) {
      this.showErrorSnackbar('Warenkorb ist leer');
      return;
    }

    const boughtItems = items.map(i => ({...i})) as CartItem[];

    const ticketIds = items.filter(isTicketItem).map(i => i.ticketId);
    const hasTickets = ticketIds.length > 0;
    const hasMerch = items.some(isMerchItem);
    const hasRewards = items.some(isRewardItem);

    const ridRaw = sessionStorage.getItem('reservation.pay.rid');
    const rid = ridRaw ? Number(ridRaw) : null;

    let allIds: number[] = [];
    let selectedIds: number[] = [];

    try {
      allIds = JSON.parse(sessionStorage.getItem('reservation.pay.allIds') ?? '[]');
      selectedIds = JSON.parse(sessionStorage.getItem('reservation.pay.selectedIds') ?? '[]');
    } catch {
      allIds = [];
      selectedIds = [];
    }

    const cleanupReservationPayFlags = () => {
      sessionStorage.removeItem('reservation.pay.rid');
      sessionStorage.removeItem('reservation.pay.allIds');
      sessionStorage.removeItem('reservation.pay.selectedIds');
    };

    const doCreateOrder = () => {
      this.orders.createFromCart(items).pipe(
        switchMap(order => {
          if (!hasTickets || rid == null || Number.isNaN(rid)) {
            cleanupReservationPayFlags();
            return of(order);
          }

          const boughtIds = (selectedIds?.length ? selectedIds : ticketIds);
          const all = (allIds?.length ? allIds : ticketIds);
          const unboughtIds = all.filter(tid => !boughtIds.includes(tid));

          const release$ = unboughtIds.length
            ? forkJoin(unboughtIds.map(tid => this.ticketsService.releaseHold(tid).pipe(catchError(() => of(null)))))
            : of([]);

          const deleteReservation$ = this.reservationsService.delete(rid).pipe(catchError(() => of(null)));

          return forkJoin({release: release$, del: deleteReservation$}).pipe(
            tap(() => cleanupReservationPayFlags()),
            map(() => order)
          );
        })
      ).subscribe({
        next: (order) => {
          this.cart.clear();
          this.rewardService.loadPoints();

          const type =
            hasTickets && !hasMerch ? 'tickets' :
              !hasTickets && hasMerch ? 'merch' :
                'tickets';

          this.router.navigate(['/invoice', order.id], {
            queryParams: {type},
            state: {
              order,
              items: boughtItems,
              payment: this.selectedPayment
            }
          });
        },
        error: err => {
          console.error(err);
          const msg =
            err?.error?.message ??
            (Array.isArray(err?.error?.errors) ? err.error.errors.join('\n') : null) ??
            'Bestellung fehlgeschlagen';
          this.showErrorSnackbar(msg);
        }
      });
    };

    if (!hasTickets) {
      doCreateOrder();
      return;
    }

    forkJoin(
      ticketIds.map(id => this.ticketsService.getTicketById(id).pipe(catchError(() => of(null))))
    ).subscribe({
      next: (ticketList) => {
        const tickets = ticketList.filter((t): t is any => t !== null);

        const statusOf = (t: any) =>
          String(t?.status ?? t?.state ?? t?.ticketStatus ?? '').toUpperCase();

        const purchasedIds = tickets
          .filter(t => statusOf(t).includes('PURCHASE'))
          .map(t => t.id);

        const notReservedIds = tickets
          .filter(t => {
            const s = statusOf(t);
            if (!s) return false;
            const isReserved = s.includes('RESERV') || s.includes('HOLD');
            const isPurchased = s.includes('PURCHASE');
            return !isReserved && !isPurchased;
          })
          .map(t => t.id);

        const badIds = Array.from(new Set([...purchasedIds, ...notReservedIds]));

        if (badIds.length > 0) {
          badIds.forEach(id => this.cart.removeTicket(id));

          this.showErrorSnackbar(
            `Einige Tickets sind nicht mehr reserviert oder bereits gekauft.\n` +
            `Entfernt aus Warenkorb: ${badIds.join(', ')}\n` +
            `Bitte Reservierungen neu laden.`
          );
          return;
        }

        doCreateOrder();
      },
      error: (e) => {
        console.error(e);
        this.showErrorSnackbar('Tickets konnten nicht geprüft werden');
      }
    });
  }

  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }

  private showErrorSnackbar(message: string): void {
    this.snackBar.open(message, 'Schließen', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['error-snackbar'],
    });
  }

}
