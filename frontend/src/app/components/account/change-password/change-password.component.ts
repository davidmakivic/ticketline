import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {AuthService} from "../../../services/auth.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-change-password',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatError,
    MatButton,
    MatInput,
    MatProgressSpinner
  ],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss',
  standalone: true
})
export class ChangePasswordComponent implements OnInit {

  form: FormGroup;
  errors: string[] = [];
  resetToken: string;
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group(
      {
        currentPassword: ['', [Validators.required, Validators.maxLength(32)]],
        newPassword:  ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
        confirmNewPassword: ['', Validators.required]
      },
      {
        validators: this.passwordMatchValidator
      }
    );
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.resetToken = params['token'];

      const currentPasswordCtrl = this.form.get('currentPassword');

      if (this.authService.isLoggedIn()) {
        currentPasswordCtrl?.setValidators([
          Validators.required,
          Validators.maxLength(32)
        ]);
      } else {
        currentPasswordCtrl?.clearValidators();
        currentPasswordCtrl?.setValidators([
          Validators.maxLength(32)
        ]);
      }

      currentPasswordCtrl?.updateValueAndValidity();

      console.log('Reset token:', this.resetToken);
    });
  }

  get currentPassword() {
    return this.form.get('currentPassword');
  }

  get newPassword(): AbstractControl {
    return this.form.get('newPassword')!;
  }

  get confirmNewPassword(): AbstractControl {
    return this.form.get('confirmNewPassword')!;
  }

  /* Custom Validator */
  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const newPassword = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmNewPassword')?.value;

    if (!newPassword || !confirmPassword) {
      return null;
    }

    if (newPassword !== confirmPassword) {
      group.get('confirmNewPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    return null;
  }

  changePassword(): void {
    this.errors = [];

    if (this.form.invalid) {
      return;
    }
    this.loading = true;
    const payload = {
      oldPassword: this.currentPassword.value,
      newPassword: this.newPassword.value,
      resetToken: this.resetToken,
    };

    console.log(this.resetToken);

    this.userService.changePassword(payload).subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open(
          'Passwort wurde erfolgreich geändert.',
          'OK',
          { duration: 4000 }
        );

        if (!this.authService.isLoggedIn()) {
          this.router.navigate(['/login']);
          return;
        }
        this.router.navigate(['/account']);
      },
      error: err => {
        this.loading = false;
        this.errors.push(err.error);
      }
    })

    console.log('Change password payload:', payload);
  }

  cancel(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/account']);
    } else {
      this.router.navigate(['/login']);
    }
  }

  protected isLoggedIn() {
    return this.authService.isLoggedIn();
  }
}
