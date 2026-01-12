import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { ReservationDto } from '../../dtos/reservation.dto';

type State = { reservation?: ReservationDto };

@Component({
  selector: 'app-reservation-success',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './reservation-success.component.html',
  styleUrls: ['./reservation-success.component.scss']
})
export class ReservationSuccessComponent {
  reservation?: ReservationDto;

  constructor(private router: Router) {
    const state = history.state as State;
    this.reservation = state.reservation;
  }

  backHome() {
    this.router.navigate(['/']);
  }
}
