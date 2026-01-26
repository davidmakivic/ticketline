package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

public interface NewsService {

    /**
     * Find all news entries ordered by published date (descending).
     *
     * @return ordered list of all news entries
     */
    List<News> findAll();

    /**
     * Find a single news entry by id.
     *
     * @param id the id of the news entry
     * @return the news entry
     */
    News findOne(Long id);

    /**
     * Publish a new news message with optional image and event association.
     *
     * @param title the title of the news
     * @param summary a brief summary of the news
     * @param text the full text content of the news
     * @param image optional image file to attach to the news
     * @param eventId optional id of an event to associate with the news
     * @return the published news entry
     * @throws IOException if image processing fails
     */
    News publishMessage(String title, String summary, String text, MultipartFile image, Long eventId) throws IOException;

    /**
     * Get all unread news for a specific user.
     *
     * @param userId the id of the user
     * @return list of unread news entries ordered by published date (descending)
     */
    List<News> getUnreadNews(Long userId);

    /**
     * Get all read news for a specific user.
     *
     * @param userId the id of the user
     * @return list of read news entries ordered by published date (descending)
     */
    List<News> getReadNews(Long userId);

    /**
     * Mark a news entry as read for a specific user.
     * If already marked as read, this operation has no effect.
     *
     * @param userId the id of the user
     * @param newsId the id of the news entry to mark as read
     */
    void markAsRead(Long userId, Long newsId);

    ResponseEntity<StreamingResponseBody> streamNewsImage(Long id);
}
