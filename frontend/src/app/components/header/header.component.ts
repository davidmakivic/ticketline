import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {EventAutocompleteDto, EventDto} from '../../dtos/event'
import {debounceTime, Subject} from "rxjs";
import {ArtistsService} from "../../services/artists.service";
import {EventsService} from "../../services/events.service";
import {ArtistAutocompleteDto} from "../../dtos/artist";
import {Router} from "@angular/router";
@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss'],
    standalone: false
})
export class HeaderComponent implements OnInit {
  constructor(
    public authService: AuthService,
    private artistsService: ArtistsService,
    private eventsService: EventsService,
    private router: Router
  ) { }

  searchTerm = '';
  searchChangedObservable = new Subject<void>();
  artists: ArtistAutocompleteDto[] = [];
  events: EventAutocompleteDto[] = [];


  ngOnInit() {
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadAutocompleteOptions()})
  }


  searchChanged() {
    this.searchChangedObservable.next();

  }

  private reloadAutocompleteOptions() {
    this.artistsService.getArtistAutoCompleteByName(this.searchTerm, 5)
      .subscribe({
        next: data => {
          this.artists = data;

          for (const artist of this.artists) {
            this.artistsService.getArtistImage(artist.id).subscribe(async blob => {
              artist.image = URL.createObjectURL(blob);
              }
            )
          }

        }
      });

    this.eventsService.getArtistAutoCompleteByName(this.searchTerm, 5)
      .subscribe({
        next: data => {
          this.events = data;

          for (const event of this.events) {
            this.eventsService.getEventImage(event.id).subscribe(async blob => {
                event.image = URL.createObjectURL(blob);
              }
            )
          }

        }
      });

  }

  protected openArtistDetail(id: number) {
    this.router.navigate(['/artists', id]);
  }

  protected openEventDetail(id: number) {
    this.router.navigate(['/events', id]);
  }
}
