import { Component, Renderer2, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

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

  constructor(private router: Router, private renderer: Renderer2) {
    const state = history.state as State;

    this.cancellation = state.cancellation;
    this.customerName = state.customerName ?? 'Kunde';
    this.originalInvoiceNo = state.originalInvoiceNo ?? '';

    const dt = this.cancellation?.createdAt ? new Date(this.cancellation.createdAt) : new Date();
    this.stornoNo = this.buildStornoNumber(this.cancellation?.orderId ?? 0, dt);
    this.stornoDateStr = this.formatDate(dt);

    this.renderer.addClass(document.body, 'invoice-print');
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
}
