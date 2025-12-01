import { NgModule } from '@angular/core';
import { mapToCanActivate, RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { MessageComponent } from './components/message/message.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'message',
    canActivate: mapToCanActivate([AuthGuard]),
    component: MessageComponent
  },
  {
    path: 'orders',
    canActivate: mapToCanActivate([AuthGuard]),
    loadComponent: () =>
      import('./components/orders/orders.component')
        .then(m => m.OrdersComponent)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
