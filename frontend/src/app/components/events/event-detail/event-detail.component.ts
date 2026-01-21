import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, RouterModule} from '@angular/router';
import {EventDto, EventTypeDto} from "../../../dtos/event";
import {EventsService} from "../../../services/events.service";
import {MatCardModule} from "@angular/material/card";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {PerformanceCardComponent} from '../../performance/performance-card/performance-card.component';


interface EventWithImage extends EventDto {
  imageUrl?: SafeUrl;
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
export class EventDetailComponent implements OnInit {
  loading = false;
  event: EventWithImage | null = null;

  constructor(
    private eventService: EventsService,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.loadEvent(id);
      }
    });
  }

  private loadEvent(id: number): void {
    this.loading = true;
    this.eventService.getEventById(id).subscribe({
      next: (event: EventDto) => {
        event.performances = [...event.performances].sort(
          (a, b) =>
            new Date(a.startTime).getTime() -
            new Date(b.startTime).getTime()
        );

        this.event = event;
        this.loadEventImage(id);
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  private loadEventImage(id: number): void {
    this.eventService.getEventImage(id).subscribe({
      next: (blob: Blob) => {
        if (this.event) {
          const url = URL.createObjectURL(blob);
          this.event.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        }
      },
      error: () => {
        // Bild konnte nicht geladen werden, ignorieren
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
