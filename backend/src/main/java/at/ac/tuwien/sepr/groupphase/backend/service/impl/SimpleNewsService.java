package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.FileStorageProperties;
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
    FileStorageProperties fileStorageProperties;

    public SimpleNewsService(NewsRepository newsRepository, EventRepository eventRepository, ReadNewsRepository readNewsRepository, UserRepository userRepository, FileStorageProperties fileStorageProperties) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.readNewsRepository = readNewsRepository;
        this.userRepository = userRepository;
        this.fileStorageProperties = fileStorageProperties;
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
            String filename = storeNewsImage(image);
            news.setImagePath(filename);
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
    public ResponseEntity<String> getNewsImagePath(Long id) {
        LOGGER.info("Returning news image path for id={}", id);

        News news = newsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("News not found: " + id));

        String path = news.getImagePath();
        if (path == null || path.isBlank()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(path);
    }


    private String storeNewsImage(MultipartFile image) throws IOException {
        var dir = java.nio.file.Paths.get(fileStorageProperties.getNewsImagePath());
        java.nio.file.Files.createDirectories(dir);

        String original = image.getOriginalFilename();
        String ext = "";
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }
        }

        String filename = java.util.UUID.randomUUID() + ext;

        try (var in = image.getInputStream()) {
            java.nio.file.Files.copy(in, dir.resolve(filename),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        return filename;
    }

}
