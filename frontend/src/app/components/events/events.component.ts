import { Component } from '@angular/core';
import {Event} from "../../dtos/event";
import {EventsService} from "../../services/events.service";

@Component({
  selector: 'app-events',
  imports: [],
  templateUrl: './events.component.html',
  styleUrl: './events.component.scss',
})
export class EventsComponent {

  loading = false;
  events: Event[] = [];

  constructor(private eventService: EventsService) {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.eventService.getEvents().subscribe({next: (events: Event[]) => this.events = events});
  }

}
