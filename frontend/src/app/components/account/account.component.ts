import {Component, OnInit} from '@angular/core';
import {Roles, User, UserDto} from "../../dtos/user";
import {MatCard, MatCardActions, MatCardContent, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {Router, RouterLink} from "@angular/router";
import {MatDivider} from "@angular/material/divider";
import {UserService} from "../../services/user.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../confirm-dialog/confirm-dialog.component";
import {AuthService} from "../../services/auth.service";

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
    private dialog : MatDialog,
    private userService: UserService,
    private router: Router,
    public authService: AuthService
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


  confirmDeleteAccount(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      backdropClass: 'confirm-dialog-backdrop',
      panelClass: 'confirm-dialog-panel',
      data: {
        title: 'Account löschen',
        message: 'Bist du sicher, dass du deinen Account endgültig löschen möchtest?',
        confirmText: 'Account löschen',
        cancelText: 'Abbrechen',
        danger: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteAccount();
      }
    });
  }

  private deleteAccount() {
    this.userService.deleteUser(this.userDetails.userId).subscribe({
      next: () => {
        this.authService.logoutUser();
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.log(err)
      }
    })
  }

  protected isAdmin(): boolean {
    return this.authService.getUserRole() == Roles.ADMIN;
  }

  protected readonly Roles = Roles;
}
