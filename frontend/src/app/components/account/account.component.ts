import {Component, OnInit} from '@angular/core';
import {User, UserDto} from "../../dtos/user";
import {MatCard, MatCardActions, MatCardContent, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {Router, RouterLink} from "@angular/router";
import {MatDivider} from "@angular/material/divider";
import {UserService} from "../../services/user.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-account',
  imports: [
    MatCardTitle,
    MatCard,
    MatCardContent,
    MatCardActions,
    MatIcon,
    MatButton,
    RouterLink,
    MatDivider,
    MatProgressSpinner
  ],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss',
  standalone: true
})
export class AccountComponent implements OnInit {

  constructor(
    private userService: UserService,
  ) {}

  userDetails: UserDto;

  ngOnInit(): void {
    this.userService.getUser().subscribe({
      next: (user: UserDto) => {
        this.userDetails = user;
      }
      }
    )
  }


}
