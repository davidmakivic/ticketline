package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
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

    public SimpleNewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public List<News> findAll() {
        LOGGER.debug("Find all news");
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    @Override
    public News findOne(Long id) {
        LOGGER.debug("Find news with id {}", id);
        Optional<News> message = newsRepository.findById(id);
        if (message.isPresent()) {
            return message.get();
        } else {
            throw new NotFoundException(String.format("Could not find news post with id %s", id));
        }
    }

    @Override
    public News publishMessage(String title, String summary, String text, MultipartFile image) throws IOException {
        News news = new News();
        news.setTitle(title);
        news.setSummary(summary);
        news.setText(text);
        news.setPublishedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            news.setImageData(image.getBytes());
            news.setImageContentType(image.getContentType());
        }

        return newsRepository.save(news);
    }

    @Override
    public ResponseEntity<byte[]> getNewsImage(Long id) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("News not found: " + id));

        if (news.getImageData() == null || news.getImageData().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(news.getImageContentType()))
            .body(news.getImageData());
    }


}
