import { NgModule } from '@angular/core';
import { mapToCanActivate, RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { NewsComponent } from './components/news/news.component';
import {EventsListComponent} from "./components/events/events-list/events-list.component";
import {EventDetailComponent} from "./components/events/event-detail/event-detail.component";
import {ArtistDetailComponent} from "./components/artists/artist-detail/artist-detail.component";
import {ArtistsListComponent} from "./components/artists/artists-list/artists-list.component";



const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'news',
    canActivate: mapToCanActivate([AuthGuard]),
    component: NewsComponent
  },
  {
    path: 'orders',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/orders/orders.component')
        .then(m => m.OrdersComponent)
  },
  {
    path: 'events',
    canActivate: mapToCanActivate([AuthGuard]),
    component: EventsListComponent,
  },
  {
    path: 'events/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: EventDetailComponent
  },
  {
    path: 'artists',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ArtistsListComponent,
  },
  {
    path: 'artists/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ArtistDetailComponent
  },
  {
    path: 'tickets',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/tickets/tickets.component')
        .then(m => m.TicketsComponent)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}

