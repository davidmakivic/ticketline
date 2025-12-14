import {Component} from '@angular/core';
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";
import {EventDto, EventTop10Dto, EventType} from "../../../dtos/event";
import {EventsService} from "../../../services/events.service";
import {forkJoin, map, Observable, of, switchMap} from "rxjs";
import {DomSanitizer} from "@angular/platform-browser";
import {RouterLink} from "@angular/router";

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
export class Top10Component {

  constructor(
    private eventsService: EventsService,
    private sanitizer: DomSanitizer

  ) {
  }

  selectedCategory = "ALL";

  top10Events: EventTop10Dto[];




  ngOnInit() {
    this.fetchTop10();
  }

  private fetchTop10(): void {
    let eventsObservable!: Observable<EventTop10Dto[]>;

    switch (this.selectedCategory) {
      case "CONCERT":
        eventsObservable = this.eventsService.getTop10Events(EventType.CONCERT);
        break;
      case "FESTIVAL":
        eventsObservable = this.eventsService.getTop10Events(EventType.FESTIVAL);
        break;
      default:
        eventsObservable = this.eventsService.getTop10Events(null);
    }

    eventsObservable.subscribe({
      next: value => {
        this.top10Events = value.slice().sort((a,b)=> b.soldTickets - a.soldTickets)
        this.loadEventImages();
      },
      error: err => {
        console.log(err);
      }
    })

  }

  private loadEventImages(): void {
    this.top10Events.forEach(event => {
      console.log(event);
      this.eventsService.getEventImage(event.eventId).subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          event.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
        }
      });
    });
  }

  protected onCategoryChange() {
    this.fetchTop10();
  }


  //Chart
  get maxTickets(): number {
    return Math.max(...this.top10Events.map(e => e.soldTickets));
  }
  getBarWidth(event: EventTop10Dto): number {
    return (event.soldTickets / this.maxTickets) * 100;
  }



}
