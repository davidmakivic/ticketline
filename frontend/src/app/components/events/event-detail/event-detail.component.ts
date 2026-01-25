import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { EventDto, EventTypeDto } from "../../../dtos/event";
import { EventsService } from "../../../services/events.service";
import { MatCardModule } from "@angular/material/card";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { DomSanitizer, SafeUrl } from "@angular/platform-browser";
import { PerformanceCardComponent } from '../../performance/performance-card/performance-card.component';
import { Subject, takeUntil } from 'rxjs';

interface EventWithImage extends EventDto {
  imageUrl?: SafeUrl;
  _objectUrl?: string;
}

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatIcon,
    MatIconButton,
    PerformanceCardComponent
  ],
  templateUrl: './event-detail.component.html',
  styleUrl: './event-detail.component.scss',
})
export class EventDetailComponent implements OnInit, OnDestroy {
  loading = false;
  event: EventWithImage | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private eventService: EventsService,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const id = Number(params['id']);
        if (id) {
          this.cleanupEventUrl();
          this.loadEvent(id);
        }
      });
  }

  ngOnDestroy(): void {
    this.cleanupEventUrl();

    this.destroy$.next();
    this.destroy$.complete();
  }

  private cleanupEventUrl(): void {
    if (this.event?._objectUrl) {
      URL.revokeObjectURL(this.event._objectUrl);
      this.event._objectUrl = undefined;
    }
    if (this.event) {
      this.event.imageUrl = undefined;
    }
  }

  private loadEvent(id: number): void {
    this.loading = true;

    this.eventService.getEventById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (event: EventDto) => {
          event.performances = [...event.performances].sort(
            (a, b) =>
              new Date(a.startTime).getTime() -
              new Date(b.startTime).getTime()
          );

          this.event = event as EventWithImage;
          this.loadEventImage(id);
          this.loading = false;
        },
        error: () => (this.loading = false)
      });
  }

  private loadEventImage(id: number): void {
    this.eventService.getEventImage(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          if (!this.event) return;

          if (this.event._objectUrl) {
            URL.revokeObjectURL(this.event._objectUrl);
            this.event._objectUrl = undefined;
          }

          const url = URL.createObjectURL(blob);
          this.event._objectUrl = url;
          this.event.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          this.cleanupEventUrl();
        }
      });
  }

  eventTypeLabels: { [key: string]: string } = {
    'CONCERT': 'KONZERT',
    'FESTIVAL': 'FESTIVAL',
    'MUSICAL': 'MUSICAL'
  };

  getEventTypeLabel(type: EventTypeDto | string): string {
    return this.eventTypeLabels[type] || type;
  }
}
