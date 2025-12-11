package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class NewsDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 5;
    private static final String TEST_NEWS_TITLE = "Title";
    private static final String TEST_NEWS_SUMMARY = "Summary of the news message";
    private static final String TEST_NEWS_TEXT = "This is the text of the news message";

    private final NewsRepository newsRepository;

    public NewsDataGenerator(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (!newsRepository.findAll().isEmpty()) {
            LOGGER.debug("news post already generated");
        } else {
            LOGGER.debug("generating {} news entries", NUMBER_OF_MESSAGES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
                News news = News.NewsBuilder.aMessage()
                    .withTitle(TEST_NEWS_TITLE + " " + i)
                    .withSummary(TEST_NEWS_SUMMARY + " " + i)
                    .withText(TEST_NEWS_TEXT + " " + i)
                    .withPublishedAt(LocalDateTime.now().minusMonths(i))
                    .build();
                LOGGER.debug("saving news post {}", news);
                newsRepository.save(news);
            }
        }
    }

}
