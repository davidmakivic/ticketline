import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MerchandiseService } from '../../services/merchandise.service';
import { MerchandiseDto } from '../../dtos/merchandise';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MerchandiseDialogComponent } from './shop-dialog.component';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, MatPaginatorModule],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss'
})
export class ShopComponent implements OnInit, OnDestroy {
  merchandise: MerchandiseDto[] = [];

  images = new Map<number, SafeUrl>();

  private imageObjectUrls = new Map<number, string>();

  pageSize = 12;
  pageIndex = 0;

  private destroy$ = new Subject<void>();

  constructor(
    private merchService: MerchandiseService,
    private sanitizer: DomSanitizer,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadMerchandise();
  }

  ngOnDestroy(): void {
    this.revokeAllImages();
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadMerchandise(): void {
    this.revokeAllImages();

    this.merchService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe(items => {
        this.merchandise = items.filter(m => m.quantity > 0);
        this.loadImages();
      });
  }

  private loadImages(): void {
    this.merchandise.forEach(item => {
      this.merchService.getImage(item.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(blob => {
          const existing = this.imageObjectUrls.get(item.id);
          if (existing) {
            URL.revokeObjectURL(existing);
            this.imageObjectUrls.delete(item.id);
            this.images.delete(item.id);
          }

          const url = URL.createObjectURL(blob);
          this.imageObjectUrls.set(item.id, url);
          this.images.set(item.id, this.sanitizer.bypassSecurityTrustUrl(url));
        });
    });
  }

  private revokeAllImages(): void {
    for (const url of this.imageObjectUrls.values()) {
      URL.revokeObjectURL(url);
    }
    this.imageObjectUrls.clear();
    this.images.clear();
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
