package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.entity.ReadNews;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReadNewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.projection.NewsImageProjection;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleNewsService implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final ReadNewsRepository readNewsRepository;
    private final UserRepository userRepository;

    public SimpleNewsService(NewsRepository newsRepository, EventRepository eventRepository, ReadNewsRepository readNewsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.readNewsRepository = readNewsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<News> findAll() {
        LOGGER.info("Fetching all news");
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    @Override
    public News findOne(Long id) {
        LOGGER.info("Fetching news with id={}", id);
        Optional<News> message = newsRepository.findById(id);
        if (message.isPresent()) {
            return message.get();
        } else {
            throw new NotFoundException(String.format("Could not find news post with id %s", id));
        }
    }

    @Override
    public News publishMessage(String title, String summary, String text, MultipartFile image, Long eventId) throws IOException {
        LOGGER.info("Publishing news: {}", title);
        News news = new News();
        news.setTitle(title);
        news.setSummary(summary);
        news.setText(text);
        news.setPublishedAt(LocalDateTime.now());

        if (eventId != null) {
            Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
            news.setEvent(event);
        }

        if (image != null && !image.isEmpty()) {
            byte[] bytes = image.getBytes();
            try {
                news.setImageData(new SerialBlob(bytes));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            news.setImageContentType(image.getContentType());
        }

        return newsRepository.save(news);
    }

    public List<News> getUnreadNews(Long userId) {
        List<Long> readNewsIds = readNewsRepository.findReadNewsIdsByUserId(userId);
        if (readNewsIds == null || readNewsIds.isEmpty()) {
            return newsRepository.findAllByOrderByPublishedAtDesc();
        }
        return newsRepository.findByIdNotInOrderByPublishedAtDesc(readNewsIds);
    }

    public List<News> getReadNews(Long userId) {
        List<Long> readNewsIds = readNewsRepository.findReadNewsIdsByUserId(userId);
        return newsRepository.findByIdInOrderByPublishedAtDesc(readNewsIds);
    }

    @Transactional
    public void markAsRead(Long userId, Long newsId) {
        if (!readNewsRepository.existsByUserIdAndNewsId(userId, newsId)) {
            ReadNews readNews = new ReadNews();
            readNews.setUser(userRepository.getReferenceById(userId));
            readNews.setNews(newsRepository.getReferenceById(newsId));
            readNews.setReadAt(LocalDateTime.now());
            readNewsRepository.save(readNews);
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> streamNewsImage(Long id) {
        LOGGER.info("Streaming news image for id={}", id);

        String contentType = newsRepository.findImageContentTypeById(id)
            .orElseThrow(() -> new NotFoundException("News not found: " + id));

        StreamingResponseBody body = outputStream -> {
            try {
                writeNewsImageTo(id, outputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(body);
    }

    @Transactional(readOnly = true)
    public void writeNewsImageTo(Long id, OutputStream out) throws Exception {
        NewsImageProjection p = newsRepository.findImageById(id)
            .orElseThrow(() -> new NotFoundException("Merchandise not found: " + id));

        Blob blob = p.getImageData();
        if (blob == null || blob.length() == 0) {
            return;
        }

        try (InputStream in = blob.getBinaryStream()) {
            in.transferTo(out);
        }
    }


}
