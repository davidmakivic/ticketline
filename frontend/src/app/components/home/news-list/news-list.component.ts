import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDividerModule } from '@angular/material/divider';
import { Router } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { NewsService } from '../../../services/news.service';
import { News } from '../../../dtos/news';

interface NewsWithImage extends News {
  imageUrl?: SafeUrl;
}

@Component({
  selector: 'app-news-list',
  standalone: true,
  imports: [CommonModule, MatDividerModule],
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.scss',
})
export class NewsListComponent implements OnInit {
  news: NewsWithImage[] = [];
  loading = false;

  constructor(
    private newsService: NewsService,
    private router: Router,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadNews();
  }

  openNewsDetails(newsId: number): void {
    this.router.navigate(['/news', newsId]);
  }

  private loadNews(): void {
    this.loading = true;
    this.newsService.getMessage().subscribe({
      next: (news: NewsWithImage[]) => {
        this.news = news;
        this.news.forEach(item => this.loadNewsImage(item));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  private loadNewsImage(news: NewsWithImage): void {
    if (news.imageContentType) {
      this.newsService.getNewsImage(news.id).subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          news.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          // Bild konnte nicht geladen werden
        }
      });
    }
  }
}
