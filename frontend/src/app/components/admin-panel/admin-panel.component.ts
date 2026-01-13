import {Component} from "@angular/core";
import { Router } from '@angular/router';

@Component({
  selector: 'admin-panel',
  standalone: true,
  imports: [

  ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.scss',
})

export class AdminPanelComponent {
  loading = false;

  constructor(
    private router: Router
  ) {

  }

  goToBanUsers() {
    this.router.navigate(['/panel/ban-users']);
  }
}
