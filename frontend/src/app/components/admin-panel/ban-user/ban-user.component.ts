import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, startWith } from 'rxjs/operators';
import { PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { UserService } from '../../../services/user.service';
import {UserDto, UserStatus} from '../../../dtos/user';

@Component({
  selector: 'app-ban-users',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatButtonModule,
    MatPaginatorModule
  ],
  templateUrl: './ban-user.component.html',
  styleUrls: ['./ban-user.component.scss']
})
export class BanUsersComponent {
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
        // Status lokal ändern
        u.userStatus = blocked ? UserStatus.UNLOCKED : UserStatus.LOCKED;

        this.busy.delete(id);
        // optional: wenn du sicherstellen willst, dass Backend/UI immer sync ist:
        // this.loadPage();
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
}
