import {SimpleEventDto} from './event';

export class News {
  id: number;
  title: string;
  summary: string;
  text: string;
  publishedAt: string;
  imagePath?: string;
  event?: SimpleEventDto;

  get eventId(): number | undefined {
    return this.event?.id;
  }

  get eventTitle(): string | undefined {
    return this.event?.title;
  }
}
