import {BrowserModule} from '@angular/platform-browser';
import {LOCALE_ID, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {NewsComponent} from './components/news/news.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatToolbar} from "@angular/material/toolbar";
import {MatAutocomplete, MatAutocompleteTrigger, MatOptgroup, MatOption} from "@angular/material/autocomplete";
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatDivider} from "@angular/material/divider";
import {Top10Component} from "./components/home/top10/top10.component";
import {NewsListComponent} from "./components/home/news-list/news-list.component";
import {NgOptimizedImage, registerLocaleData} from "@angular/common";
import { MatTooltipModule } from '@angular/material/tooltip';
import localeDe from '@angular/common/locales/de';

registerLocaleData(localeDe);

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    NewsComponent,
  ],
  bootstrap: [AppComponent],
  imports: [BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    MatTooltipModule,
    NgbModule,
    FormsModule,
    MatButton,
    MatToolbar,
    MatAutocompleteTrigger,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatIconButton, MatAutocomplete, MatOption, MatOptgroup, MatDivider, Top10Component, NewsListComponent, Top10Component, HomeComponent, NgOptimizedImage],
  exports: [
    Top10Component
  ],
  providers: [httpInterceptorProviders, provideHttpClient(withInterceptorsFromDi()),{ provide: LOCALE_ID, useValue: 'de-DE' }]
})
export class AppModule {
}
