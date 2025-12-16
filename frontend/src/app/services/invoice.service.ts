import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import { CartItem } from '../dtos/cart-item';
import { OrderDto } from '../dtos/order.dto';

@Injectable({ providedIn: 'root' })
export class InvoiceService {

  download(order: OrderDto, items: CartItem[]) {
    const doc = new jsPDF();

    doc.text('Ticketline Rechnung', 20, 20);
    doc.text(`Order #${order.id}`, 20, 30);

    let y = 45;
    items.forEach(i => {
      doc.text(`${i.quantity}x ${i.title}`, 20, y);
      doc.text(`${(i.priceCents / 100).toFixed(2)} €`, 160, y);
      y += 10;
    });

    doc.text(`Gesamt: ${(order.totalPriceCents / 100).toFixed(2)} €`, 20, y + 10);
    doc.save(`ticketline-${order.id}.pdf`);
  }
}
