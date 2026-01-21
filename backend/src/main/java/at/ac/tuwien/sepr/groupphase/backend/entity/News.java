package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "published_at")
    private LocalDateTime publishedAt;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 10000)
    private String text;

    @Column(name = "imageData", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "image_content_type")
    private String imageContentType;

    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News news)) {
            return false;
        }
        return Objects.equals(id, news.id)
            && Objects.equals(publishedAt, news.publishedAt)
            && Objects.equals(title, news.title)
            && Objects.equals(summary, news.summary)
            && Objects.equals(text, news.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publishedAt, title, summary, text);
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", publishedAt=" + publishedAt
            + ", title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + '}';
    }


    public static final class NewsBuilder {
        private Long id;
        private LocalDateTime publishedAt;
        private String title;
        private String summary;
        private String text;
        private Event event;

        private NewsBuilder() {
        }

        public static NewsBuilder aMessage() {
            return new NewsBuilder();
        }

        public NewsBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NewsBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public NewsBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public NewsBuilder withEvent(Event event) {
            this.event = event;
            return this;
        }

        public News build() {
            News news = new News();
            news.setId(id);
            news.setPublishedAt(publishedAt);
            news.setTitle(title);
            news.setSummary(summary);
            news.setText(text);
            news.setEvent(event);
            return news;
        }
    }
}