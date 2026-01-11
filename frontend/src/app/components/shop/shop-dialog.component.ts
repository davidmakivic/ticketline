import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MerchandiseDto } from '../../dtos/merchandise';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  templateUrl: './shop-dialog.component.html',
  styleUrl: './shop-dialog.component.scss'
})
export class MerchandiseDialogComponent {
  quantity = 1;
  selectedSize?: string;

  constructor(@Inject(MAT_DIALOG_DATA) public item: MerchandiseDto) {}

  increase(): void {
    if (this.quantity < this.item.quantity) this.quantity++;
  }

  decrease(): void {
    if (this.quantity > 1) this.quantity--;
  }

  addToCart(): void {
    console.log('Add to cart', {
      item: this.item,
      quantity: this.quantity,
      size: this.selectedSize
    });
  }
}
