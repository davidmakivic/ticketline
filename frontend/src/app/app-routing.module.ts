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
import { CheckoutGuard } from './guards/checkout.guard';





const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'news',
    canActivate: mapToCanActivate([AuthGuard]),
    component: NewsComponent
  },
  {
    path: 'cart',
    loadComponent: () =>
      import('./components/cart/cart.component').then(m => m.CartComponent)
  },
  {
    path: 'cart/checkout',
    canActivate: mapToCanActivate([CheckoutGuard]),
    loadComponent: () =>
      import('./components/cart/checkout.component').then(m => m.CheckoutComponent)
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
    path: 'performances/:performanceId/seats',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/seat-selection/seat-selection.component')
        .then(m => m.SeatSelectionComponent)
  },
  {
    path: 'tickets',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/tickets/tickets.component')
        .then(m => m.TicketsComponent)
  },
  {
    path: '**',
    redirectTo: ''
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}

