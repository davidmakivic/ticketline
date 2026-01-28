import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {News} from '../dtos/news';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsBaseUri: string = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all messages from the backend
   */
  getMessage(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri);
  }

  /**
   * Loads specific message from the backend
   *
   * @param id of message to load
   */
  getMessageById(id: number): Observable<News> {
    console.log('Load message details for ' + id);
    return this.httpClient.get<News>(this.newsBaseUri + '/' + id);
  }

  createMessage(news: News, image?: File, eventId?: number): Observable<News> {
    const formData = new FormData();
    formData.append('title', news.title);
    formData.append('summary', news.summary);
    formData.append('text', news.text);
    if (image) {
      formData.append('image', image);
    }
    if (eventId) {
      formData.append('eventId', eventId.toString());
    }
    return this.httpClient.post<News>(this.newsBaseUri, formData);
  }

  getNewsImage(filename: string): Observable<Blob> {
    return this.httpClient.get(
      `${this.globals.backendUri}/files/news-images/${encodeURIComponent(filename)}`,
      { responseType: 'blob' }
    );
  }

  getImageURL(filename: string): string {
    return `${this.globals.backendUri}/files/news-images/${encodeURIComponent(filename)}`;
  }

  getUnreadNews(): Observable<News[]> {
    const params = new HttpParams().set('status', 'unread');
    return this.httpClient.get<News[]>(this.newsBaseUri, { params });
  }

  getReadNews(): Observable<News[]> {
    const params = new HttpParams().set('status', 'read');
    return this.httpClient.get<News[]>(this.newsBaseUri, { params });
  }

  markAsRead(newsId: number): Observable<void> {
    return this.httpClient.put<void>(`${this.newsBaseUri}/${newsId}/read-status`, null);
  }
}
