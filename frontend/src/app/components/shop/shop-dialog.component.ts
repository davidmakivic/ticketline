import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchandiseDto, MerchandiseVariantDto } from '../../dtos/merchandise';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  templateUrl: './shop-dialog.component.html',
  styleUrls: ['./shop-dialog.component.scss']
})
export class MerchandiseDialogComponent {
  quantity: number | null = 1;
  selectedSize!: MerchandiseVariantDto; // wird beim Laden gesetzt
  errorMessage?: string;

  constructor(@Inject(MAT_DIALOG_DATA) public item: MerchandiseDto) {
    // Artikel mit Größen -> automatisch erste Variante auswählen
    if (this.hasSizes()) {
      this.selectedSize = this.item.variants[0];
    } else {
      // One-Size Artikel -> erste und einzige Variante auswählen
      this.selectedSize = this.item.variants[0];
    }
  }

  /** Gibt true zurück, wenn der Artikel echte Größen hat */
  hasSizes(): boolean {
    return this.item.variants.some(v => v.size !== null);
  }

  get maxQuantity(): number {
    return this.selectedSize.quantity;
  }

  onSizeChange(): void {
    this.quantity = 1;
    this.errorMessage = undefined;
    this.validateQuantity();
  }

  increase(): void {
    if (this.quantity === null) this.quantity = 1;
    if (this.quantity < this.maxQuantity) this.quantity++;
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
    if (!this.quantity || this.quantity <= 0) {
      this.errorMessage = 'Bitte wähle eine Stückzahl größer 0.';
      this.quantity = 1;
    } else {
      this.validateQuantity();
    }
  }

  private validateQuantity(): void {
    if (!this.quantity || this.quantity <= 0) {
      this.errorMessage = 'Die Stückzahl muss mindestens 1 sein.';
    } else if (this.quantity > this.maxQuantity) {
      this.errorMessage = `Es sind maximal ${this.maxQuantity} Stück verfügbar.`;
    } else {
      this.errorMessage = undefined;
    }
  }

  addToCart(): void {
    this.validateQuantity();
    if (this.errorMessage) return;

    console.log('Add to cart', {
      itemId: this.item.id,
      size: this.selectedSize.size,
      quantity: this.quantity
    });
  }
}
