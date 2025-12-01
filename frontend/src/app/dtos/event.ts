export interface Event {
  id: number;
  title: string;
  description: string;
  category: EventType;
  durationMinutes: number;
}

enum EventType {
  CONCERT,
  FESTIVAL
}
