export interface Event {
  id: number;
  title: string;
  description: string;
  category: EventType;
  durationMinutes: number;
}

export interface TopEvent {
  title: string,
  soldTickets: number;
}

export enum EventType {
  CONCERT,
  FESTIVAL
}
