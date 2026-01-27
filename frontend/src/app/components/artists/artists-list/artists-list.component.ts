import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ArtistsService } from '../../../services/artists.service';
import { Artist } from '../../../dtos/artist';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {Subject, takeUntil} from "rxjs";

interface ArtistWithImage extends Artist {
  imageUrl?: SafeUrl;
}

@Component({
  selector: 'app-artists-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './artists-list.component.html',
  styleUrl: './artists-list.component.scss',
})
export class ArtistsListComponent implements OnInit {
  loading = false;
  artists: ArtistWithImage[] = [];
  private artistImageObjectUrls: Map<number, string> = new Map();
  private destroy$ = new Subject<void>();

  constructor(
    private artistsService: ArtistsService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.artistsService.getArtists().pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (artists: Artist[]) => {
        this.artists = artists;
        this.loadArtistImages();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  private loadArtistImages(): void {
    this.artists.forEach(artist => {
      this.artistsService.getArtistImage(artist.id).pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          this.artistImageObjectUrls.set(artist.id, url);
          artist.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          // Bild konnte nicht geladen werden, ignorieren
        }
      });
    });
  }

  ngOnDestroy(): void {
    for (const url of this.artistImageObjectUrls.values()) {
      URL.revokeObjectURL(url);
    }
    this.artistImageObjectUrls.clear();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
