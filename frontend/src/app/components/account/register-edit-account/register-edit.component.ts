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
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {ErrorStateMatcher} from "@angular/material/core";
import {MatError, MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {count, Observable, startWith} from "rxjs";
import {EUROPEAN_COUNTRIES} from './european-countries';
import {MatSelectModule} from '@angular/material/select';
import {UserService} from "../../../services/user.service";
import {MatAutocompleteModule, MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {map} from "rxjs/operators";
import {AsyncPipe} from "@angular/common";


export enum RegisterEditMode {
  register,
  edit
}

export const passwordMatchValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const password = group.get('password');
  const confirmPassword = group.get('confirmPassword');

  if (!password || !confirmPassword) {
    return null;
  }

  if (confirmPassword.errors && !confirmPassword.errors['passwordMismatch']) {
    return null;
  }

  if (password.value !== confirmPassword.value) {
    confirmPassword.setErrors({passwordMismatch: true});
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
  selector: 'app-register',
  templateUrl: './register-edit.component.html',
  styleUrls: ['./register-edit.component.scss'],
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
export class RegisterEditComponent implements OnInit {

  mode: RegisterEditMode = RegisterEditMode.register;

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

  errors: string[] = [];


  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private userService: UserService,
    private route: ActivatedRoute,
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
      confirmPassword: this.confirmPassword
    }, {validators: passwordMatchValidator});
  }

  ngOnInit() {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === RegisterEditMode.edit) {
        this.disablePasswordValidation();
        this.initData();
      }
    });
    this.filteredCountries = this.country.valueChanges.pipe(
      startWith(''),
      map(value => this.filterCountries(value))
    );
  }


  makeRequest() {
    if (this.registerForm.invalid) {
      console.log("Invalid input");
      return;
    }

    if (this.mode === RegisterEditMode.edit) {
      const payload = {
        firstName: this.firstName.value,
        lastName: this.lastName.value,
        email: this.email.value,
        country: this.country.value,
        zipCode: this.zip.value,
        city: this.city.value,
        street: this.street.value,
        houseNumber: Number(this.houseNumber.value),
        role: 'USER'
      };

      this.userService.updateUser(payload).subscribe({
        next: () => {
          this.errors = [];
          this.router.navigate(['/']);
        },
        error: error => {
          console.log('Could not log in due to:');
          console.log(error);
          if (typeof error.error === 'object') {
            this.errors = error.error.errors;
          } else {
            this.errors = error.errors;
          }
        }
      })
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
      street: this.street.value,
      houseNumber: Number(this.houseNumber.value),
      role: 'USER'
    };

    this.userService.createUser(payload).subscribe({
      next: () => {
        this.errors = [];
        this.router.navigate(['/login']);
      },
      error: error => {
        console.log('Could not log in due to:');
        console.log(error);
        if (typeof error.error === 'object') {
          this.errors = error.error.errors;
        } else {
          this.errors = error.errors;
        }
      }
    })
  }

  private filterCountries(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.countries.filter(country =>
      country.toLowerCase().includes(filterValue)
    );
  }

  public modeText(): string {
    console.log(this.mode)
    switch (this.mode) {
      case RegisterEditMode.edit:
        return "Bearbeiten";
      case RegisterEditMode.register:
        return "Registrieren";
    }
  }


  protected cancel()  {
    this.router.navigate(['/account']);
  }

  protected readonly count = count;
  protected readonly name = name;
  protected readonly confirm = confirm;
  protected readonly RegisterEditMode = RegisterEditMode;

  private initData() {
    this.userService.getUser().subscribe({
      next: data => {
        console.log(data);
        this.registerForm.patchValue({
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.email,
          country: data.country,
          zip: data.zipCode,
          city: data.city,
          street: data.street,
          houseNumber: data.houseNumber,
        })
      }
    })
  }

  private disablePasswordValidation() {
    this.password.clearValidators();
    this.confirmPassword.clearValidators();

    this.password.updateValueAndValidity();
    this.confirmPassword.updateValueAndValidity();

    this.registerForm.clearValidators();
    this.registerForm.updateValueAndValidity();
  }
}

export default RegisterEditComponent
