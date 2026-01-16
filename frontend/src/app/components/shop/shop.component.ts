import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MerchandiseService } from '../../services/merchandise.service';
import { MerchandiseDto } from '../../dtos/merchandise';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MerchandiseDialogComponent } from './shop-dialog.component';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, MatPaginatorModule],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss'
})
export class ShopComponent implements OnInit {
  merchandise: MerchandiseDto[] = [];
  images = new Map<number, SafeUrl>();

  pageSize = 12;
  pageIndex = 0;

  constructor(
    private merchService: MerchandiseService,
    private sanitizer: DomSanitizer,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadMerchandise();
  }

  loadMerchandise(): void {
    this.merchService.getAll().subscribe(items => {
      this.merchandise = items.filter(m => m.quantity > 0);
      this.loadImages();
    });
  }

  loadImages(): void {
    this.merchandise.forEach(item => {
      this.merchService.getImage(item.id).subscribe(blob => {
        const url = URL.createObjectURL(blob);
        this.images.set(
          item.id,
          this.sanitizer.bypassSecurityTrustUrl(url)
        );
      });
    });
  }

  get pagedItems(): MerchandiseDto[] {
    const start = this.pageIndex * this.pageSize;
    return this.merchandise.slice(start, start + this.pageSize);
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
  }

  openDialog(item: MerchandiseDto): void {
    this.dialog.open(MerchandiseDialogComponent, {
      width: '420px',
      data: item
    });
  }

  getImage(id: number): SafeUrl | undefined {
    return this.images.get(id);
  }

  hasSizes(item: MerchandiseDto): boolean {
    return (item.variants ?? []).some(v => v.size !== null);
  }
}
