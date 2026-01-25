package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@DependsOn("eventDataGenerator")
@Component
public class NewsDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;

    public NewsDataGenerator(NewsRepository newsRepository, EventRepository eventRepository) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void generateMessage() {
        if (!newsRepository.findAll().isEmpty()) {
            LOGGER.debug("News already generated");
            return;
        }

        LOGGER.debug("Generating realistic news entries");

        News news1 = News.NewsBuilder.aMessage()
            .withTitle("Queen Tribute Show angekündigt")
            .withSummary("Spektakuläre Live-Neuinterpretation der legendären Rockband kommt nach Österreich")
            .withText("Die weltberühmte Queen Tribute Band wird im September 2025 exklusive Auftritte in Wien und Salzburg geben. "
                + "Mit authentischen Arrangements und energiegeladenen Performances werden die größten Hits von Freddie Mercury und der Band präsentiert. "
                + "Tickets sind ab sofort im Vorverkauf erhältlich.")
            .withPublishedAt(LocalDateTime.now().minusDays(5))
            .build();
        loadImageFromFile(news1, new ClassPathResource("images/queen-tribute.jpg"));

        News news2 = News.NewsBuilder.aMessage()
            .withTitle("Elektro Beats Festival 2025 – Lineup bekannt gegeben")
            .withSummary("Internationale Top-DJs und Künstler auf der größten Elektro-Veranstaltung des Jahres")
            .withText("Das Elektro Beats Festival präsentiert ein weltklasse Lineup mit den besten DJs und Elektronikmusikunternehmen. "
                + "Vom 15. bis 17. August findet das Festival auf dem Gelände des ehemaligen Flughafens statt. "
                + "Über 300 Acts werden über vier Tage hinweg auftreten. Early Bird Tickets sind um 15% günstiger erhältlich.")
            .withPublishedAt(LocalDateTime.now().minusDays(3))
            .withEvent(eventRepository.getReferenceById(6L))
            .build();
        loadImageFromFile(news2, new ClassPathResource("images/Elektro-Beats-Festival.jpg"));

        News news3 = News.NewsBuilder.aMessage()
            .withTitle("Mamma Mia! bricht alle Besucherrekorde")
            .withSummary("Das beliebte ABBA-Musical in Wien zeigt Rekordbesucherzahlen")
            .withText("Das Musical Mamma Mia! hat in der Wiener Staatsoper alle bisherigen Besucherrekorde gebrochen. "
                + "Mit über 95% Auslastung in den letzten drei Wochen begeistert die Produktion täglich tausende Besucher. "
                + "Die Verantwortlichen kündigten bereits zwei Zusatztermine an, um der großen Nachfrage gerecht zu werden.")
            .withPublishedAt(LocalDateTime.now().minusDays(2))
            .withEvent(eventRepository.getReferenceById(8L))
            .build();
        loadImageFromFile(news3, new ClassPathResource("images/mamma-mia.jpg"));

        News news4 = News.NewsBuilder.aMessage()
            .withTitle("Indie Summer Festival 2025 erweitert Programm")
            .withSummary("Neue internationale Künstler und lokale Talente ergänzen das vielfältige Musikfestival")
            .withText("Das Indie Summer Festival gab heute die Erweiterung des diesjährigen Lineups bekannt. "
                + "Zusätzlich zu den bereits bekannten Headlinern treten nun auch 25 neue Künstler auf. "
                + "Das Festival findet vom 22. bis 24. Juni am Donauinselpark statt und soll ein breites Publikum ansprechen.")
            .withPublishedAt(LocalDateTime.now().minusDays(1))
            .withEvent(eventRepository.getReferenceById(10L))
            .build();
        loadImageFromFile(news4, new ClassPathResource("images/Indie-Summer-Festival.jpg"));

        News news5 = News.NewsBuilder.aMessage()
            .withTitle("Rock am Ring – Größtes Konzert des Jahres angekündigt")
            .withSummary("Internationale Rocklegenden treten zum ersten Mal gemeinsam auf")
            .withText("Rock am Ring präsentiert in diesem Jahr das größte Konzert in der Veranstaltungsgeschichte. "
                + "Legendäre Rockbands werden erstmals gemeinsam auf einer Bühne auftreten. "
                + "Mit erwarteten 50.000 Besuchern wird es das Highlight des Musikjahres. "
                + "Tickets sind seit gestern verfügbar und bereits zu 60% ausverkauft.")
            .withPublishedAt(LocalDateTime.now())
            .withEvent(eventRepository.getReferenceById(3L))
            .build();
        loadImageFromFile(news5, new ClassPathResource("images/Rock-am-Ring-24.png"));

        newsRepository.saveAll(java.util.List.of(news1, news2, news3, news4, news5));

        LOGGER.debug("5 news entries generated successfully");
    }

    private void loadImageFromFile(News news, ClassPathResource img) {
        try (InputStream in = img.getInputStream()) {

            byte[] bytes = in.readAllBytes();
            news.setImageData(bytes);
            news.setImageContentType("image/jpeg");
        } catch (IOException e) {
            LOGGER.warn("Could not load image from {}: {}", img, e.getMessage());
        }
    }
}
