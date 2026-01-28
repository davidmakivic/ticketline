import {Component} from '@angular/core';
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-forgot-password',
  imports: [
    MatFormField,
    FormsModule,
    MatLabel,
    ReactiveFormsModule,
    MatError,
    MatButton,
    MatInput,
    RouterLink,
    MatProgressSpinner
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
  standalone: true,

})
export class ForgotPasswordComponent {
  email = new FormControl('', [Validators.required, Validators.email]);
  loading = false;

  constructor(
    private router: Router,
    private userService: UserService,
    private snackBar: MatSnackBar,
  ) {
  }

  resetPassword() {
    if (this.email.invalid) {
      return;
    }

    const emailValue = this.email.value;
    this.loading = true;

    this.userService.resetPassword(emailValue).subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open(
          'Eine Email zum Zurücksetzten des Passwortes wurde gesendet.',
          'OK',
          {duration: 4000}
        );
        this.router.navigate(['/login'], {state: {passwordResetRequested: true}});

      },
      error: err => {
        this.snackBar.open(
          'Es ist ein Fehler aufgetreten. Versuchen sie es später erneut.',
          'OK',
          {duration: 4000}
        );

        this.loading = false;
      }
    })
  }
}
