import {NewsDetailComponent} from "./components/news/news-detail/news-detail.component";
import {PerformancesListComponent} from "./components/performance/performances-list/performances-list.component";
import {AdminPanelComponent} from "./components/admin-panel/admin-panel.component";
import {BanUsersComponent} from "./components/admin-panel/ban-user/ban-user.component";
import { ShopComponent } from './components/shop/shop.component';
import {ArtistDetailComponent} from "./components/artists/artist-detail/artist-detail.component";
import {ArtistsListComponent} from "./components/artists/artists-list/artists-list.component";
import {mapToCanActivate, RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {LoginComponent} from "./components/login/login.component";
import RegisterComponent from "./components/register/register.component";
import {NewsComponent} from "./components/news/news.component";
import {AuthGuard} from "./guards/auth.guard";
import {EventDetailComponent} from "./components/events/event-detail/event-detail.component";
import {EventsListComponent} from "./components/events/events-list/events-list.component";
import {NgModule} from "@angular/core";


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {
    path: 'news',
    component: NewsComponent
  },
  {
    path: 'news/:id',
    component: NewsDetailComponent
  },

  {
    path: 'cart',
    loadComponent: () =>
      import('./components/cart/cart.component')
        .then(m => m.CartComponent)
  },

  {
    path: 'storno-invoice/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/storno-invoice/storno-invoice.component')
        .then(m => m.StornoInvoiceComponent)
  },


  {
    path: 'checkout',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/checkout/checkout.component')
        .then(m => m.CheckoutComponent)
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
    component: EventsListComponent
  },

  {
    path: 'events/:id',
    component: EventDetailComponent
  },

  {
    path: 'shop',
    component: ShopComponent
  },

  {
    path: 'artists',
    component: ArtistsListComponent
  },

  {
    path: 'artists/:id',
    component: ArtistDetailComponent
  },

  {
    path: 'performances',
    component: PerformancesListComponent
  },

  {
    path: 'performances/:performanceId/seats',
    loadComponent: () =>
      import('./components/seat-selection/seat-selection.component')
        .then(m => m.SeatSelectionComponent)
  },



  {
    path: 'panel',
    component: AdminPanelComponent
  },

  {
    path: 'panel/ban-users',
    component: BanUsersComponent
  },

  {
    path: 'tickets',
    loadComponent: () =>
      import('./components/tickets/tickets.component')
        .then(m => m.TicketsComponent)
  },

  {
    path: 'invoice/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/invoice/invoice.component')
        .then(m => m.InvoiceComponent)
  },

  {
    path: 'reserve/confirm',
    loadComponent: () =>
      import('./components/reservations/reservation-confirm.component')
        .then(m => m.ReservationConfirmComponent)
  },
  {
    path: 'reserve/success',
    loadComponent: () =>
      import('./components/reservations/reservation-success.component')
        .then(m => m.ReservationSuccessComponent)
  },

  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

