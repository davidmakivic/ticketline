import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDividerModule } from '@angular/material/divider';
import { NewsService } from '../../../services/news.service';
import { News } from '../../../dtos/news';

@Component({
  selector: 'app-news-list',
  standalone: true,
  imports: [CommonModule, MatDividerModule],
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.scss',
})
export class NewsListComponent implements OnInit {
  news: News[] = [];
  loading = false;

  constructor(private newsService: NewsService) {}

  ngOnInit(): void {
    this.loadNews();
  }

  private loadNews(): void {
    this.loading = true;
    this.newsService.getMessage().subscribe({
      next: (news: News[]) => {
        this.news = news;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }
}
