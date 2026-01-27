import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { NewsService } from '../../../services/news.service';
import { News } from '../../../dtos/news';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {AuthService} from "../../../services/auth.service";

interface NewsWithImage extends News {
  imageUrl?: SafeUrl;
}

@Component({
  selector: 'app-news-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIcon, MatIconButton],
  templateUrl: './news-detail.component.html',
  styleUrl: './news-detail.component.scss',
})
export class NewsDetailComponent implements OnInit {
  news: NewsWithImage | null = null;
  loading = false;

  constructor(
    private newsService: NewsService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.loadNews(id);
      }
    });
  }

  private loadNews(id: number): void {
    this.loading = true;
    this.newsService.getMessageById(id).subscribe({
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
    if (news.imagePath) {
      this.newsService.getNewsImage(news.imagePath).subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          news.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {}
      });
    }
  }

  navigateToEvent(eventId: number): void {
    this.router.navigate(['/events', eventId]);
  }

  navigateBack(): void {
    const navigation = this.router.getCurrentNavigation();
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
