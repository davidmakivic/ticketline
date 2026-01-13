import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchandiseDto } from '../../dtos/merchandise';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  templateUrl: './shop-dialog.component.html',
  styleUrls: ['./shop-dialog.component.scss']
})
export class MerchandiseDialogComponent {
  quantity: number | null = 1;
  selectedSize?: string;
  errorMessage?: string;

  constructor(@Inject(MAT_DIALOG_DATA) public item: MerchandiseDto) {}

  increase(): void {
    if (this.quantity === null) this.quantity = 1;
    if (this.quantity < this.item.quantity) this.quantity++;
    this.validateQuantity();
  }

  decrease(): void {
    if (this.quantity === null) this.quantity = 1;
    if (this.quantity > 1) this.quantity--;
    this.validateQuantity();
  }

  onQuantityChange(): void {
    this.validateQuantity();
  }

  onQuantityBlur(): void {
    if (this.quantity === null || this.quantity === 0) {
      this.errorMessage = 'Bitte wähle eine Stückzahl größer 0.';
      this.quantity = null;
    } else {
      this.validateQuantity();
    }
  }

  private validateQuantity(): void {
    if (this.quantity === null || this.quantity <= 0) {
      this.errorMessage = 'Die Stückzahl muss mindestens 1 sein.';
    } else if (this.quantity > this.item.quantity) {
      this.errorMessage = `Es sind maximal ${this.item.quantity} Stück verfügbar.`;
    } else {
      this.errorMessage = undefined;
    }
  }

  addToCart(): void {
    // Finale Sicherheit
    this.validateQuantity();
    if (this.errorMessage) return;

    console.log('Add to cart', {
      item: this.item,
      quantity: this.quantity,
      size: this.selectedSize
    });
  }
}
