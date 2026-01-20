import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, startWith } from 'rxjs/operators';
import { PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { UserService } from '../../services/user.service';
import {Roles, UserDto, UserStatus} from '../../dtos/user';
import {MatTooltip, MatTooltipModule} from "@angular/material/tooltip";
import {RouterModule} from "@angular/router";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-ban-users',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatIconModule,
    MatTooltipModule,
    MatInputModule,
    MatTableModule,
    MatButtonModule,
    MatPaginatorModule,
    MatTooltip
  ],
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent {
  loading = false;
  error: string | null = null;

  displayedColumns = ['email', 'status', 'actions'];

  searchCtrl = new FormControl<string>('', { nonNullable: true });

  pageIndex = 0;
  pageSize = 25;
  total = 0;

  users: UserDto[] = [];

  busy = new Set<number>();

  resetBusy = new Set<number>();

  constructor(private usersService: UserService, private snack: MatSnackBar) {
    this.searchCtrl.valueChanges.pipe(
      startWith(this.searchCtrl.value),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(() => {
      this.pageIndex = 0;
      this.loadPage();
    });
  }

  loadPage() {
    this.loading = true;
    this.error = null;

    const email = this.searchCtrl.value;

    this.usersService.getUsers(this.pageIndex, this.pageSize, email).subscribe({
      next: (page) => {
        this.users = page.content;
        this.total = page.totalElements;
        this.loading = false;
      },
      error: (e) => {
        console.error(e);
        this.error = 'Konnte Nutzer nicht laden.';
        this.loading = false;
      }
    });
  }

  onPageChange(ev: PageEvent) {
    this.pageIndex = ev.pageIndex;
    this.pageSize = ev.pageSize;
    this.loadPage();
  }

  onResetPassword(u: UserDto) {
    const id = this.getUserId(u);
    const email = u.email;

    if (!id || !email || this.resetBusy.has(id)) return;

    this.resetBusy.add(id);
    this.error = null;

    this.usersService.resetPassword(email).subscribe({
      next: () => {
        this.resetBusy.delete(id);
        this.snack.open('Passwort-Reset wurde ausgelöst (E-Mail wurde gesendet).', 'OK', {duration: 3000});
      },
      error: (e) => {
        console.error(e);
        this.resetBusy.delete(id);
        this.snack.open('Passwort-Reset fehlgeschlagen.', 'OK', {duration: 3000});
      }
    });
  }

  isBlocked(u: UserDto): boolean {
    return u.userStatus === UserStatus.LOCKED;
  }

  toggleBlock(u: UserDto) {
    const id = this.getUserId(u);
    if (!id || this.busy.has(id)) return;

    const blocked = this.isBlocked(u);
    this.busy.add(id);
    this.error = null;

    const call$ = blocked
      ? this.usersService.unblockUser(id)
      : this.usersService.blockUser(id);

    call$.subscribe({
      next: () => {

        u.userStatus = blocked ? UserStatus.UNLOCKED : UserStatus.LOCKED;

        this.busy.delete(id);
      },
      error: (e) => {
        console.error(e);
        this.error = blocked ? 'Entsperren fehlgeschlagen.' : 'Sperren fehlgeschlagen.';
        this.busy.delete(id);
      }
    });
  }


  getUserId(u: UserDto): number {
    return (u as any).userId ?? (u as any).id;
  }

  isAdmin(u: UserDto) {
    return u.role === Roles.ADMIN;
  }

  actionLabel(u: UserDto): string {
    return this.isBlocked(u) ? 'Entsperren' : 'Sperren';
  }
}
