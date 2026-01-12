import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { CartService } from '../../services/cart.service';
import { OrdersService } from '../../services/order.service';
import { TicketsService } from '../../services/tickets.service';
import { Ticket } from '../../dtos/ticket';

import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

type PaymentId = 'card' | 'paypal' | 'klarna' | 'applepay';
import { ReservationsService } from '../../services/reservations.service';


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
    { id: 'card', label: 'Kreditkarte', logo: '/assets/payments/visa-mastercard.svg' },
    { id: 'paypal', label: 'PayPal', logo: '/assets/payments/paypal.svg' },
    { id: 'klarna', label: 'Klarna', logo: '/assets/payments/klarna.svg' },
    { id: 'applepay', label: 'Apple Pay', logo: '/assets/payments/apple-pay.svg' }
  ];

  readonly totalCents$ = this.cart.cartItems$.pipe(
    map(items => items.map(i => i.ticketId)),
    switchMap(ids => {
      if (ids.length === 0) return of([] as Ticket[]);
      return forkJoin(
        ids.map(id =>
          this.ticketsService.getTicketById(id).pipe(
            catchError(() => of(null))
          )
        )
      ).pipe(
        map(list => list.filter((t): t is Ticket => t !== null))
      );
    }),
    map(tickets =>
      tickets.reduce((sum, t) => sum + (t.priceFinalCents ?? 0), 0)
    )
  );

  constructor(
    public cart: CartService,
    private ticketsService: TicketsService,
    private orders: OrdersService,
    private router: Router,
     private reservationsService: ReservationsService
  ) {}

  select(method: PaymentId): void {
    this.selectedPayment = method;
  }

 pay(): void {
   const items = this.cart.getCartItems();
   if (items.length === 0) {
     alert('Warenkorb ist leer');
     return;
   }

   const boughtItems = items.map(i => ({ ...i }));
   const ids = items.map(i => i.ticketId);

   const ridRaw = sessionStorage.getItem('reservation.deleteAfterPay');
   const rid = ridRaw ? Number(ridRaw) : null;

   forkJoin(
     ids.map(id =>
       this.ticketsService.getTicketById(id).pipe(
         catchError(() => of(null))
       )
     )
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

         alert(
           `Einige Tickets sind nicht mehr reserviert oder bereits gekauft.\n` +
           `Entfernt aus Warenkorb: ${badIds.join(', ')}\n` +
           `Bitte Reservierungen neu laden.`
         );
         return;
       }

       this.orders.createFromCart(items).pipe(
         switchMap(order => {
           if (rid == null || Number.isNaN(rid)) return of(order);

           return this.reservationsService.delete(rid).pipe(
             catchError(() => of(null)),
             tap(() => sessionStorage.removeItem('reservation.deleteAfterPay')),
             map(() => order)
           );
         })
       ).subscribe({
         next: (order) => {
           this.cart.clear();

           this.router.navigate(['/invoice', order.id], {
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
           alert(msg);
         }
       });
     },
     error: (e) => {
       console.error(e);
       alert('Tickets konnten nicht geprüft werden');
     }
   });
 }


  toEuro(cents: number): string {
    return (cents / 100).toFixed(2).replace('.', ',') + ' €';
  }
}
