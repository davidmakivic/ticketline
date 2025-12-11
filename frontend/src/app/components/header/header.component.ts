import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {EventDto, EventType} from '../../dtos/event'
@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss'],
    standalone: false
})
export class HeaderComponent implements OnInit {

  constructor(public authService: AuthService) { }

  ngOnInit() {
  }
  artists = ["The Electric Owls", "Luna Harmony", "Dj Thunderstrike"];
  events: EventDto[] = [
    {
      id: 1,
      title: 'Rock am Ring',
      description: 'Großes jährliches Rockfestival mit internationalen Headlinern.',
      category: EventType.FESTIVAL,
      durationMinutes: 720,
      artists: [],
      performances: []
    },
    {
      id: 2,
      title: 'Symphonic Night Vienna',
      description: 'Konzertabend mit klassischer Musik im Wiener Konzerthaus.',
      category: EventType.CONCERT,
      durationMinutes: 120,
      artists: [],
      performances: []
    },
    {
      id: 3,
      title: 'Electronic Summer Bash',
      description: 'Open-Air EDM Event mit bekannten DJs.',
      category: EventType.FESTIVAL,
      durationMinutes: 480,
      artists: [],
      performances: []
    }
  ]
}
