import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ArtistsService } from '../../../services/artists.service';
import { Artist } from '../../../dtos/artist';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Subject, takeUntil } from 'rxjs';

interface ArtistWithImage extends Artist {
  imageUrl?: SafeUrl;
  _objectUrl?: string;
}

@Component({
  selector: 'app-artists-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './artists-list.component.html',
  styleUrl: './artists-list.component.scss',
})
export class ArtistsListComponent implements OnInit, OnDestroy {
  loading = false;
  artists: ArtistWithImage[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private artistsService: ArtistsService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.load();
  }

  ngOnDestroy(): void {
    this.revokeAllArtistUrls();

    this.destroy$.next();
    this.destroy$.complete();
  }

  load(): void {
    this.loading = true;
    this.revokeAllArtistUrls();

    this.artistsService.getArtists()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (artists: Artist[]) => {
          this.artists = artists as ArtistWithImage[];
          this.loadArtistImages();
          this.loading = false;
        },
        error: () => (this.loading = false)
      });
  }

  private revokeAllArtistUrls(): void {
    this.artists.forEach(a => this.revokeArtistUrl(a));
  }

  private revokeArtistUrl(artist: ArtistWithImage): void {
    if (artist._objectUrl) {
      URL.revokeObjectURL(artist._objectUrl);
      artist._objectUrl = undefined;
    }
    artist.imageUrl = undefined;
  }

  private loadArtistImages(): void {
    this.artists.forEach(artist => {
      this.artistsService.getArtistImage(artist.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (blob: Blob) => {
            this.revokeArtistUrl(artist);

            const url = URL.createObjectURL(blob);
            artist._objectUrl = url;
            artist.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
          },
          error: () => {
            this.revokeArtistUrl(artist);
          }
        });
    });
  }
}
