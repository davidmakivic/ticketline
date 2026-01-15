import { Component } from '@angular/core';
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
import {Router} from "@angular/router";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-change-password',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatError,
    MatButton,
    MatInput
  ],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss',
  standalone: true
})
export class ChangePasswordComponent {

  form: FormGroup;
  errors: string[] = [];


  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
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

    const payload = {
      oldPassword: this.currentPassword.value,
      newPassword: this.newPassword.value
    };

    this.userService.changePassword(payload).subscribe({
      next: () => {
        this.router.navigate(['/account']);
      },
      error: err => {
        console.log(err);
        this.errors = err.error.errors;
      }
    })

    console.log('Change password payload:', payload);
  }

  cancel(): void {
    this.router.navigate(['/account']);
  }
}
