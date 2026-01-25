import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Artist } from '../../../dtos/artist';
import { EventDto } from '../../../dtos/event';
import { ArtistsService } from '../../../services/artists.service';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { EventsService } from "../../../services/events.service";
import { Subject, takeUntil } from 'rxjs';

interface EventWithImage extends EventDto {
  imageUrl?: SafeUrl;
  _objectUrl?: string;
}

interface ArtistWithImage extends Artist {
  imageUrl?: SafeUrl;
  _objectUrl?: string;
}

@Component({
  selector: 'app-artist-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatProgressSpinnerModule, MatButtonModule, MatIconModule],
  templateUrl: './artist-detail.component.html',
  styleUrl: './artist-detail.component.scss',
})
export class ArtistDetailComponent implements OnInit, OnDestroy {
  loading = false;
  artist: ArtistWithImage | null = null;
  events: EventWithImage[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private artistsService: ArtistsService,
    private eventService: EventsService,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const id = Number(params['id']);
        if (id) {

          this.cleanupAllObjectUrls();

          this.loadArtist(id);
          this.loadArtistEvents(id);
        }
      });
  }

  ngOnDestroy(): void {
    this.cleanupAllObjectUrls();

    this.destroy$.next();
    this.destroy$.complete();
  }

  private cleanupAllObjectUrls(): void {
    this.cleanupArtistUrl();
    this.cleanupEventUrls();
  }

  private cleanupArtistUrl(): void {
    if (this.artist?._objectUrl) {
      URL.revokeObjectURL(this.artist._objectUrl);
      this.artist._objectUrl = undefined;
    }
    if (this.artist) {
      this.artist.imageUrl = undefined;
    }
  }

  private cleanupEventUrls(): void {
    this.events.forEach(e => {
      if (e._objectUrl) {
        URL.revokeObjectURL(e._objectUrl);
        e._objectUrl = undefined;
      }
      e.imageUrl = undefined;
    });
  }

  private loadArtist(id: number): void {
    this.loading = true;

    this.artistsService.getArtistById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (artist: Artist) => {
          this.artist = artist as ArtistWithImage;
          this.loadArtistImage(id);
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  private loadArtistImage(id: number): void {
    this.artistsService.getArtistImage(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          if (!this.artist) return;

          if (this.artist._objectUrl) {
            URL.revokeObjectURL(this.artist._objectUrl);
            this.artist._objectUrl = undefined;
          }

          const url = URL.createObjectURL(blob);
          this.artist._objectUrl = url;
          this.artist.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          this.cleanupArtistUrl();
        }
      });
  }

  private loadArtistEvents(id: number): void {
    this.artistsService.getArtistEvents(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (events: EventDto[]) => {
          this.cleanupEventUrls();

          this.events = events as EventWithImage[];
          this.loadEventImages();
        },
        error: () => {
          console.error('Events konnten nicht geladen werden');
        }
      });
  }

  private loadEventImages(): void {
    this.events.forEach(event => {
      this.eventService.getEventImage(event.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (blob: Blob) => {
            if (event._objectUrl) {
              URL.revokeObjectURL(event._objectUrl);
              event._objectUrl = undefined;
            }

            const url = URL.createObjectURL(blob);
            event._objectUrl = url;
            event.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
          },
          error: () => {
            if (event._objectUrl) {
              URL.revokeObjectURL(event._objectUrl);
              event._objectUrl = undefined;
            }
            event.imageUrl = undefined;
          }
        });
    });
  }
}
