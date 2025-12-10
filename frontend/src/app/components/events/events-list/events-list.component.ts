import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {EventsService} from "../../../services/events.service";
import {EventDto} from "../../../dtos/event";
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {debounceTime, distinctUntilChanged, Subject} from "rxjs";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";

interface EventWithImage extends EventDto {
  imageUrl?: SafeUrl;
}

@Component({
  selector: 'app-events-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './events-list.component.html',
  styleUrl: './events-list.component.scss',
})
export class EventsListComponent implements OnInit {
  loading = false;
  events: EventWithImage[] = [];
  searchTitle = '';
  private searchSubject = new Subject<string>();

  constructor(
    private eventService: EventsService,
    private sanitizer: DomSanitizer
  ) {
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(title => {
      this.performSearch(title);
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.eventService.getEvents().subscribe({
      next: (events: EventDto[]) => {
        this.events = events;
        this.loadEventImages();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  search(): void {
    this.searchSubject.next(this.searchTitle);
  }

  private performSearch(title: string): void {
    if (!title.trim()) {
      this.load();
      return;
    }
    this.loading = true;
    this.eventService.searchEventsByTitle(title).subscribe({
      next: (events: EventDto[]) => {
        this.events = events;
        this.loadEventImages();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  private loadEventImages(): void {
    this.events.forEach(event => {
      this.eventService.getEventImage(event.id).subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          event.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          // Bild konnte nicht geladen werden, ignorieren
        }
      });
    });
  }
}
