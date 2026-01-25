package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.entity.ReadNews;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReadNewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
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
            news.setImageData(image.getBytes());
            news.setImageContentType(image.getContentType());
        }

        return newsRepository.save(news);
    }

    @Override
    public ResponseEntity<byte[]> getNewsImage(Long id) {
        LOGGER.info("Fetching news image for id={}", id);
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("News not found: " + id));

        if (news.getImageData() == null || news.getImageData().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(news.getImageContentType()))
            .body(news.getImageData());
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


}
