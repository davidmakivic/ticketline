import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDividerModule } from '@angular/material/divider';
import { Router } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { NewsService } from '../../../services/news.service';
import { News } from '../../../dtos/news';
import { Subject, takeUntil } from 'rxjs';

interface NewsWithImage extends News {
  imageUrl?: SafeUrl;
  _objectUrl?: string;
}

@Component({
  selector: 'app-news-list',
  standalone: true,
  imports: [CommonModule, MatDividerModule],
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.scss',
})
export class NewsListComponent implements OnInit, OnDestroy {
  news: NewsWithImage[] = [];
  loading = false;

  private destroy$ = new Subject<void>();

  constructor(
    private newsService: NewsService,
    private router: Router,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadNews();
  }

  ngOnDestroy(): void {
    this.revokeAllNewsUrls();

    this.destroy$.next();
    this.destroy$.complete();
  }

  openNewsDetails(newsId: number): void {
    this.router.navigate(['/news', newsId], {
      state: { fromHomepage: true }
    });
  }

  private loadNews(): void {
    this.loading = true;

    this.revokeAllNewsUrls();

    this.newsService.getMessage()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (news: NewsWithImage[]) => {
          this.news = news as NewsWithImage[];
          this.news.forEach(item => this.loadNewsImage(item));
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        },
      });
  }

  private loadNewsImage(news: NewsWithImage): void {
    if (!news.imageContentType) return;

    this.newsService.getNewsImage(news.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          if (news._objectUrl) {
            URL.revokeObjectURL(news._objectUrl);
            news._objectUrl = undefined;
          }

          const url = URL.createObjectURL(blob);
          news._objectUrl = url;
          news.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          if (news._objectUrl) {
            URL.revokeObjectURL(news._objectUrl);
            news._objectUrl = undefined;
          }
          news.imageUrl = undefined;
        }
      });
  }

  private revokeAllNewsUrls(): void {
    this.news.forEach(n => {
      if (n._objectUrl) {
        URL.revokeObjectURL(n._objectUrl);
        n._objectUrl = undefined;
      }
      n.imageUrl = undefined;
    });
  }
}
