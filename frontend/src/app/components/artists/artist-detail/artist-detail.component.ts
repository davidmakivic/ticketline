import { Component, OnInit } from '@angular/core';
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
import {EventsService} from "../../../services/events.service";

interface EventWithImage extends EventDto {
  imageUrl?: SafeUrl;
}

interface ArtistWithImage extends Artist {
  imageUrl?: SafeUrl;
}

@Component({
  selector: 'app-artist-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatProgressSpinnerModule, MatButtonModule, MatIconModule],
  templateUrl: './artist-detail.component.html',
  styleUrl: './artist-detail.component.scss',
})
export class ArtistDetailComponent implements OnInit {
  loading = false;
  artist: ArtistWithImage | null = null;
  events: EventWithImage[] = [];

  constructor(
    private artistsService: ArtistsService,
    private eventService: EventsService,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.loadArtist(id);
        this.loadArtistEvents(id);
      }
    });
  }

  private loadArtist(id: number): void {
    this.loading = true;
    this.artistsService.getArtistById(id).subscribe({
      next: (artist: Artist) => {
        this.artist = artist;
        this.loadArtistImage(id);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private loadArtistImage(id: number): void {
    this.artistsService.getArtistImage(id).subscribe({
      next: (blob: Blob) => {
        if (this.artist) {
          const url = URL.createObjectURL(blob);
          this.artist.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        }
      },
      error: () => {
        // Bild konnte nicht geladen werden, ignorieren
      }
    });
  }

  private loadArtistEvents(id: number): void {
    this.artistsService.getArtistEvents(id).subscribe({
      next: (events: EventDto[]) => {
        this.events = events;
        this.loadEventImages();
      },
      error: () => {
        console.error('Events konnten nicht geladen werden');
      }
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
          // Bild konnte nicht geladen werden
        }
      });
    });
  }
}
