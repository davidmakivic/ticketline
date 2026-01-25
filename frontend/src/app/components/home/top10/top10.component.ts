import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatButtonToggle, MatButtonToggleGroup } from "@angular/material/button-toggle";
import { FormsModule } from "@angular/forms";
import { EventTop10Dto, EventTypeDto } from "../../../dtos/event";
import { EventsService } from "../../../services/events.service";
import { Observable, Subject, takeUntil } from "rxjs";
import { DomSanitizer, SafeUrl } from "@angular/platform-browser";
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-top10',
  imports: [
    MatButtonToggleGroup,
    MatButtonToggle,
    FormsModule,
    RouterLink
  ],
  templateUrl: './top10.component.html',
  styleUrl: './top10.component.scss',
  standalone: true
})
export class Top10Component implements OnInit, OnDestroy {

  constructor(
    private eventsService: EventsService,
    private sanitizer: DomSanitizer
  ) {}

  selectedCategory = "ALL";
  top10Events: EventTop10Dto[] = [];

  private destroy$ = new Subject<void>();

  private imageObjectUrls = new Map<number, string>();

  ngOnInit() {
    this.fetchTop10();
  }

  ngOnDestroy(): void {
    this.revokeAllImages();

    this.destroy$.next();
    this.destroy$.complete();
  }

  protected onCategoryChange() {
    this.fetchTop10();
  }

  private fetchTop10(): void {
    this.revokeAllImages();

    let eventsObservable!: Observable<EventTop10Dto[]>;

    switch (this.selectedCategory) {
      case "CONCERT":
        eventsObservable = this.eventsService.getTop10Events(EventTypeDto.CONCERT);
        break;
      case "FESTIVAL":
        eventsObservable = this.eventsService.getTop10Events(EventTypeDto.FESTIVAL);
        break;
      case "MUSICAL":
        eventsObservable = this.eventsService.getTop10Events(EventTypeDto.MUSICAL);
        break;
      default:
        eventsObservable = this.eventsService.getTop10Events(null);
    }

    eventsObservable
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: value => {
          this.top10Events = value.slice().sort((a, b) => b.soldTickets - a.soldTickets);
          this.loadEventImages();
        },
        error: err => console.log(err)
      });
  }

  private loadEventImages(): void {
    this.top10Events.forEach(event => {
      this.eventsService.getEventImage(event.eventId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (blob: Blob) => {
            const existing = this.imageObjectUrls.get(event.eventId);
            if (existing) {
              URL.revokeObjectURL(existing);
              this.imageObjectUrls.delete(event.eventId);
            }

            const url = URL.createObjectURL(blob);
            this.imageObjectUrls.set(event.eventId, url);

            (event as any).imageUrl = this.sanitizer.bypassSecurityTrustUrl(url) as SafeUrl;
          },
          error: () => {
            const existing = this.imageObjectUrls.get(event.eventId);
            if (existing) {
              URL.revokeObjectURL(existing);
              this.imageObjectUrls.delete(event.eventId);
            }
            (event as any).imageUrl = undefined;
          }
        });
    });
  }

  private revokeAllImages(): void {
    for (const url of this.imageObjectUrls.values()) {
      URL.revokeObjectURL(url);
    }
    this.imageObjectUrls.clear();
    this.top10Events.forEach(e => ((e as any).imageUrl = undefined));
  }

  // Chart
  get maxTickets(): number {
    return this.top10Events.length ? Math.max(...this.top10Events.map(e => e.soldTickets)) : 0;
  }

  getBarWidth(event: EventTop10Dto): number {
    if (!this.maxTickets) return 0;
    return (event.soldTickets / this.maxTickets) * 100;
  }
}
