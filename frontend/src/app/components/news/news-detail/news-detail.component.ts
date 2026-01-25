import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { NewsService } from '../../../services/news.service';
import { News } from '../../../dtos/news';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { AuthService } from "../../../services/auth.service";
import { Subject, takeUntil } from 'rxjs';

interface NewsWithImage extends News {
  imageUrl?: SafeUrl;    // fürs Template
  _objectUrl?: string;   // raw blob url fürs revoke
}

@Component({
  selector: 'app-news-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIcon, MatIconButton],
  templateUrl: './news-detail.component.html',
  styleUrl: './news-detail.component.scss',
})
export class NewsDetailComponent implements OnInit, OnDestroy {
  news: NewsWithImage | null = null;
  loading = false;

  private destroy$ = new Subject<void>();

  constructor(
    private newsService: NewsService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const id = Number(params['id']);
        if (id) {
          this.cleanupNewsUrl();
          this.loadNews(id);
        }
      });
  }

  ngOnDestroy(): void {
    this.cleanupNewsUrl();
    this.destroy$.next();
    this.destroy$.complete();
  }

  private cleanupNewsUrl(): void {
    if (this.news?._objectUrl) {
      URL.revokeObjectURL(this.news._objectUrl);
      this.news._objectUrl = undefined;
    }
    if (this.news) {
      this.news.imageUrl = undefined;
    }
  }

  private loadNews(id: number): void {
    this.loading = true;

    this.newsService.getMessageById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (news: News) => {
          this.news = news as NewsWithImage;
          this.loadNewsImage(this.news);
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
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
          // optional: bei Fehler freigeben
          if (news._objectUrl) {
            URL.revokeObjectURL(news._objectUrl);
            news._objectUrl = undefined;
          }
          news.imageUrl = undefined;
        }
      });
  }

  navigateToEvent(eventId: number): void {
    this.router.navigate(['/events', eventId]);
  }

  navigateBack(): void {
    const state = window.history.state;

    if (state?.fromHomepage) {
      this.router.navigate(['/']);
    } else if (this.authService.isLoggedIn()) {
      this.router.navigate(['/news']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
