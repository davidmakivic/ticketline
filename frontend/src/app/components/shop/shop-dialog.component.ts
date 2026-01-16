import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { MerchandiseDto, MerchandiseVariantDto } from '../../dtos/merchandise';
import { CartService } from '../../services/cart.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatSnackBarModule],
  templateUrl: './shop-dialog.component.html',
  styleUrls: ['./shop-dialog.component.scss']
})
export class MerchandiseDialogComponent {
  quantity: number | null = 1;
  selectedSize!: MerchandiseVariantDto;
  errorMessage?: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public item: MerchandiseDto,
    private cart: CartService,
    private snack: MatSnackBar,
    private ref: MatDialogRef<MerchandiseDialogComponent>
  ) {
    this.selectedSize = this.item.variants?.[0]!;
  }

  hasSizes(): boolean {
    return (this.item.variants ?? []).some(v => v.size !== null);
  }

  get maxQuantity(): number {
    return this.selectedSize?.quantity ?? 0;
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

    const v = this.selectedSize;
    this.cart.addMerch({
      merchandiseId: this.item.id,
      variantId: v.id,
      name: this.item.name,
      size: v.size ?? null,
      unitPriceCents: this.item.price ?? 0,
      quantity: this.quantity ?? 1
    });

    this.snack.open('In den Warenkorb hinzugefügt', 'OK', { duration: 1800 });
    this.ref.close(true);
  }
}
