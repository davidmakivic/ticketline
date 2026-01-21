import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroupDirective,
  NgForm,
  ReactiveFormsModule,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {ErrorStateMatcher} from '@angular/material/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {Observable, startWith} from 'rxjs';
import {EUROPEAN_COUNTRIES} from '../register-edit-account/european-countries';
import {MatSelectModule} from '@angular/material/select';
import {UserService} from '../../../services/user.service';
import {MatAutocompleteModule, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import {map} from 'rxjs/operators';
import {AsyncPipe} from '@angular/common';
import {Roles, UserCreateDto} from '../../../dtos/user';

export const passwordMatchValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const password = group.get('password');
  const confirmPassword = group.get('confirmPassword');

  if (!password || !confirmPassword) return null;

  if (confirmPassword.errors && !confirmPassword.errors['passwordMismatch']) return null;

  if (password.value !== confirmPassword.value) {
    confirmPassword.setErrors({ passwordMismatch: true });
  } else {
    confirmPassword.setErrors(null);
  }

  return null;
};

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-panel-create-user',
  templateUrl: './admin-create-user.component.html',
  styleUrls: ['./admin-create-user.component.scss'],
  imports: [
    ReactiveFormsModule,
    MatInput,
    MatButton,
    MatLabel,
    MatFormField,
    MatError,
    MatSelectModule,
    MatAutocompleteModule,
    MatAutocompleteTrigger,
    AsyncPipe,
  ],
  standalone: true
})
export class AdminCreateUserComponent implements OnInit {

  registerForm: UntypedFormGroup;
  matcher = new MyErrorStateMatcher();

  firstName = new FormControl('', [Validators.required]);
  lastName = new FormControl('', [Validators.required]);
  country = new FormControl('', [Validators.required]);
  zip = new FormControl('', [Validators.required]);
  city = new FormControl('', [Validators.required]);
  street = new FormControl('', [Validators.required]);
  houseNumber = new FormControl('', [Validators.required]);

  email = new FormControl('', [
    Validators.required,
    Validators.email,
    Validators.pattern("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
  ]);

  password = new FormControl('', [Validators.required, Validators.minLength(8)]);
  confirmPassword = new FormControl('', [Validators.required]);

  role = new FormControl<Roles>(Roles.USER, { nonNullable: true, validators: [Validators.required] });

  countries = EUROPEAN_COUNTRIES;
  filteredCountries!: Observable<string[]>;

  errors: string[] = [];

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private userService: UserService,
  ) {
    this.registerForm = this.formBuilder.group({
      firstName: this.firstName,
      lastName: this.lastName,
      country: this.country,
      zip: this.zip,
      city: this.city,
      houseNumber: this.houseNumber,
      street: this.street,
      email: this.email,
      password: this.password,
      confirmPassword: this.confirmPassword,
      // NEU:
      role: this.role
    }, { validators: passwordMatchValidator });
  }

  ngOnInit() {
    this.filteredCountries = this.country.valueChanges.pipe(
      startWith(''),
      map(value => this.filterCountries(value ?? ''))
    );
  }

  makeRequest() {
    if (this.registerForm.invalid) return;

    const payload: UserCreateDto = {
      firstName: this.firstName.value!,
      lastName: this.lastName.value!,
      email: this.email.value!,
      password: this.password.value!,
      country: this.country.value!,
      zipCode: this.zip.value!,
      city: this.city.value!,
      street: this.street.value!,
      houseNumber: Number(this.houseNumber.value),
      role: this.role.value
    };

    this.userService.createUserAsAdmin(payload).subscribe({
      next: () => {
        this.errors = [];
        this.router.navigate(['/panel']);
      },
      error: error => {
        console.log(error);
        if (typeof error.error === 'object') {
          this.errors = error.error.errors ?? ['User konnte nicht erstellt werden'];
        } else {
          this.errors = error.errors ?? ['User konnte nicht erstellt werden'];
        }
      }
    });
  }

  private filterCountries(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.countries.filter(c => c.toLowerCase().includes(filterValue));
  }

  titleText(): string {
    return 'User erstellen';
  }

  cancel() {
    this.router.navigate(['/panel']);
  }
}
