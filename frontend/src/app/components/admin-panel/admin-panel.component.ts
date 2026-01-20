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
import {MatTooltip} from "@angular/material/tooltip";
import {RouterModule} from "@angular/router";

@Component({
  selector: 'app-ban-users',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
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

  constructor(private usersService: UserService) {
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
