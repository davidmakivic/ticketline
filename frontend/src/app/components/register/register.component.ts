import {Component, OnInit} from '@angular/core';
import {
  FormControl,
  FormGroup,
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
import {ErrorStateMatcher} from "@angular/material/core";
import {MatError, MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {count, Observable, startWith} from "rxjs";
import {EUROPEAN_COUNTRIES} from './european-countries';
import {MatSelectModule} from '@angular/material/select';
import {UserService} from "../../services/user.service";
import {MatAutocompleteModule, MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {map} from "rxjs/operators";
import {AsyncPipe} from "@angular/common";


export const passwordMatchValidator: ValidatorFn = (formGroup: FormGroup): ValidationErrors | null => {
  if (formGroup.get('password').value === formGroup.get('confirmPassword').value)
    return null;
  else
    return {passwordMismatch: true};
};

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    ReactiveFormsModule,
    MatInput,
    MatButton,
    MatLabel,
    MatFormField,
    MatError,
    MatSelectModule,
    RouterLink,
    MatAutocompleteModule,
    MatAutocompleteTrigger,
    AsyncPipe,

  ],
  standalone: true
})
export class RegisterComponent implements OnInit {

  registerForm: UntypedFormGroup;
  matcher = new MyErrorStateMatcher();

  firstName = new FormControl('', [Validators.required]);
  lastName = new FormControl('', [Validators.required]);
  country = new FormControl('', [Validators.required]);
  zip = new FormControl('', [Validators.required]);
  city = new FormControl('', [Validators.required]);
  street = new FormControl('', [Validators.required]);
  houseNumber = new FormControl('', [Validators.required]);
  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")]);
  password = new FormControl('', [Validators.required, Validators.minLength(8)]);
  confirmPassword = new FormControl('', [Validators.required]);

  countries = EUROPEAN_COUNTRIES;
  filteredCountries: Observable<string[]>;

  registerErrors: string[] = [];


  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private userService: UserService
  ) {
    this.registerForm = this.formBuilder.group({
      firstName: this.firstName,
      lastName: this.lastName,
      country: this.country,
      zip: this.zip,
      city: this.city,
      houseNumber: this.houseNumber,
      email: this.email,
      password: this.password,
      confirmPassword: this.confirmPassword
    }, {validators: passwordMatchValidator});
  }

  ngOnInit() {
    this.filteredCountries = this.country.valueChanges.pipe(
      startWith(''),
      map(value => this.filterCountries(value))
    );
  }


  onPasswordInput() {
    if (this.registerForm.hasError('passwordMissmatch')) {
      this.confirmPassword.setErrors([{'passwordMismatch': true}]);
    } else {
      this.confirmPassword.setErrors(null);
    }
  }


  registerUser() {
    if (this.registerForm.invalid) {
      console.log("Invalid input");
      return;
    }

    const payload = {
      firstName: this.firstName.value,
      lastName: this.lastName.value,
      email: this.email.value,
      password: this.password.value,
      country: this.country.value,
      zipCode: this.zip.value,
      city: this.city.value,
      address: this.street.value + " " + this.houseNumber.value,
      role: 'USER'
    };

    this.userService.createUser(payload).subscribe({
      next: () => {
        console.log("Hi")
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.log('Registration failed', error);
        this.registerErrors = error.error.errors;
        console.log(error.error.errors);
      }
    })
  }

  private filterCountries(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.countries.filter(country =>
      country.toLowerCase().includes(filterValue)
    );
  }

  protected cancel() {
    this.router.navigate(['/']);
  }

  protected readonly count = count;
  protected readonly name = name;
}

export default RegisterComponent
