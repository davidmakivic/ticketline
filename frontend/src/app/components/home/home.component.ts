import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NewsListComponent} from './news-list/news-list.component';
import {Top10Component} from "./top10/top10.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  standalone: true,
  imports: [NewsListComponent, Top10Component]
})
export class HomeComponent implements OnInit {

  constructor(public authService: AuthService) { }

  ngOnInit() {
  }

}
