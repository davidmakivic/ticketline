import {
  ChangeDetectorRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild,
  ViewChildren,
  ViewEncapsulation
} from '@angular/core';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder, NgForm} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {SafeUrl,DomSanitizer} from "@angular/platform-browser";
import {Router} from "@angular/router";
import {EventsService} from "../../services/events.service";
import {EventAutocompleteDto, EventDto, SimpleEventDto} from '../../dtos/event';

interface NewsWithImage extends News {
  imageUrl?: SafeUrl;
}

@Component({
    selector: 'app-message',
    templateUrl: './news.component.html',
    styleUrls: ['./news.component.scss'],
    standalone: false,
  encapsulation: ViewEncapsulation.None

})
export class NewsComponent implements OnInit {

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;

  currentMessage: NewsWithImage;
  selectedFile: File | null = null;
  isEditMode: boolean = false;

  availableEvents: SimpleEventDto[] = [];
  selectedEventId?: number;

  showUnreadOnly: boolean = true;
  private unreadNews: NewsWithImage[] = [];
  private readNews: NewsWithImage[] = [];

  private message: NewsWithImage[];

  constructor(private messageService: NewsService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private modalService: NgbModal,
              private sanitizer: DomSanitizer,
              private router: Router,
              private eventsService: EventsService
  ) {
  }

  ngOnInit() {
    this.loadNews();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  openAddModal(messageAddModal: TemplateRef<any>) {
    this.isEditMode = false;
    this.currentMessage = new News() as NewsWithImage;
    this.selectedFile = null;
    this.modalService.open(messageAddModal, {ariaLabelledBy: 'modal-basic-title'});
    this.loadAvailableEvents();
  }


  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  /**
   * Starts form validation and builds a message dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addMessage(form) {
    this.submitted = true;
    if (form.valid) {
      this.currentMessage.publishedAt = new Date().toISOString();
      if (this.selectedEventId) {
        const selectedEvent = this.availableEvents.find(e => e.id === this.selectedEventId);
        if (selectedEvent) {
          this.currentMessage.event = selectedEvent;
        }
      } else {
        this.currentMessage.event = undefined;
      }
      this.createMessage(this.currentMessage);
      this.clearForm();
    }
  }

  getMessage(): NewsWithImage[] {
    if (!this.isLoggedIn()) {
      return [...this.unreadNews, ...this.readNews];
    }
    return this.showUnreadOnly ? this.unreadNews : this.readNews;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  private createMessage(message: NewsWithImage) {
    this.messageService.createMessage(
      message,
      this.selectedFile || undefined,
      this.selectedEventId || undefined
    ).subscribe({
      next: () => {
        this.loadNews();
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }
  private loadNews() {
    if (this.isLoggedIn()) {
      this.loadUnreadNews();
      this.loadReadNews();
    } else {
      // Nicht eingeloggte Benutzer werden zur Login-Seite umgeleitet
      this.router.navigate(['/login']);
    }
  }


  private loadUnreadNews() {
    this.messageService.getUnreadNews().subscribe({
      next: (messages: NewsWithImage[]) => {
        this.unreadNews = messages;
        this.unreadNews.forEach(msg => this.loadNewsImage(msg));
      },
      error: error => this.defaultServiceErrorHandling(error)
    });
  }

  private loadReadNews() {
    this.messageService.getReadNews().subscribe({
      next: (messages: NewsWithImage[]) => {
        this.readNews = messages;
        this.readNews.forEach(msg => this.loadNewsImage(msg));
      },
      error: error => this.defaultServiceErrorHandling(error)
    });
  }

  private markAsRead(newsId: number) {
    this.messageService.markAsRead(newsId).subscribe({
      next: () => {
        // News von unread zu read verschieben
        const newsIndex = this.unreadNews.findIndex(n => n.id === newsId);
        if (newsIndex > -1) {
          const [news] = this.unreadNews.splice(newsIndex, 1);
          this.readNews.unshift(news);
        }
      },
      error: error => this.defaultServiceErrorHandling(error)
    });
  }

  toggleNewsView() {
    this.showUnreadOnly = !this.showUnreadOnly;
  }


  private loadNewsImage(news: NewsWithImage) {
    if (news.imageContentType) {
      this.messageService.getNewsImage(news.id).subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          news.imageUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        },
        error: () => {
          // Bild konnte nicht geladen werden
        }
      });
    }
  }

  loadAvailableEvents() {
    this.eventsService.getEvents(0, 100).subscribe({
      next: (result) => {
        this.availableEvents = result.content.map(e => ({
          id: e.id,
          title: e.title
        }));
      }
    });
  }
  openExistingMessageModal(id: number, messageAddModal: TemplateRef<any>) {
    this.router.navigate(['/news', id]);
  }


  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  private clearForm() {
    this.currentMessage = new News() as NewsWithImage;
    this.selectedFile = null;
    this.submitted = false;
  }

  navigateToEvent(eventId: number) {
    this.router.navigate(['/events', eventId]);
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

}
