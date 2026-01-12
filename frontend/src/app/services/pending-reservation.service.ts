import { Injectable } from '@angular/core';

const KEY = 'pendingReservation.ticketIds.v1';

@Injectable({ providedIn: 'root' })
export class PendingReservationService {

  setTicketIds(ids: number[]) {
    localStorage.setItem(KEY, JSON.stringify(ids ?? []));
  }

  getTicketIds(): number[] {
    try {
      const raw = localStorage.getItem(KEY);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      return Array.isArray(parsed) ? parsed.map(Number) : [];
    } catch {
      return [];
    }
  }

  clear() {
    localStorage.removeItem(KEY);
  }
}
