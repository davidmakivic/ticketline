import {ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild, ViewChildren} from '@angular/core';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder, NgForm} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {SafeUrl,DomSanitizer} from "@angular/platform-browser";

interface NewsWithImage extends News {
  imageUrl?: SafeUrl;
}

@Component({
    selector: 'app-message',
    templateUrl: './news.component.html',
    styleUrls: ['./news.component.scss'],
    standalone: false
})
export class NewsComponent implements OnInit {

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;

  currentMessage: NewsWithImage;
  selectedFile: File | null = null;
  isEditMode: boolean = false;

  private message: NewsWithImage[];

  constructor(private messageService: NewsService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private modalService: NgbModal,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.loadMessage();
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
  }

  openExistingMessageModal(id: number, messageAddModal: TemplateRef<any>) {
    this.isEditMode = true;
    this.messageService.getMessageById(id).subscribe({
      next: res => {
        this.currentMessage = res as NewsWithImage;
        this.loadNewsImage(this.currentMessage);
        this.modalService.open(messageAddModal, {ariaLabelledBy: 'modal-basic-title'});
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
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
      this.createMessage(this.currentMessage);
      this.clearForm();
    }
  }

  getMessage(): NewsWithImage[] {
    return this.message;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  private createMessage(message: NewsWithImage) {
    this.messageService.createMessage(message, this.selectedFile || undefined).subscribe({
        next: () => {
          this.loadMessage();
        },
        error: error => {
          this.defaultServiceErrorHandling(error);
        }
      }
    );
  }

  private loadMessage() {
    this.messageService.getMessage().subscribe({
      next: (messages: NewsWithImage[]) => {
        this.message = messages;
        this.message.forEach(msg => this.loadNewsImage(msg));
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
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

}
