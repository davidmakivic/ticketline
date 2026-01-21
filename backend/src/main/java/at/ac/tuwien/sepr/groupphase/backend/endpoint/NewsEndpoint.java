package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final UserRepository userRepository;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper, UserRepository userRepository) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
        this.userRepository = userRepository;
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleNewsDto> findAll() {
        LOGGER.info("Fetching all news");
        return newsMapper.newsToSimpleNewsDto(newsService.findAll());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/{id}")
    public DetailedNewsDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        DetailedNewsDto news = newsMapper.newsToDetailedNewsDto(newsService.findOne(id));

        // News als gelesen markieren
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            ApplicationUser user = userRepository.findUserByEmail(email);
            if (user != null) {
                newsService.markAsRead(user.getUserId(), id);
            }
        }

        return news;
    }

    // Ungelesene News für den aktuellen Benutzer
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/unread")
    public List<SimpleNewsDto> getUnreadNews() {
        LOGGER.info("GET /api/v1/news/unread");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);
        return newsMapper.newsListToSimpleNewsDtoList(newsService.getUnreadNews(user.getUserId()));
    }

    // Gelesene News für den aktuellen Benutzer
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/read")
    public List<SimpleNewsDto> getReadNews() {
        LOGGER.info("GET /api/v1/news/read");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);
        return newsMapper.newsToSimpleNewsDto(newsService.getReadNews(user.getUserId()));
    }

    // News als gelesen markieren
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        LOGGER.info("POST /api/v1/news/{}/read", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);
        newsService.markAsRead(user.getUserId(), id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DetailedNewsDto create(
        @RequestParam("title") String title,
        @RequestParam("summary") String summary,
        @RequestParam("text") String text,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "eventId", required = false) Long eventId) throws IOException {

        return newsMapper.newsToDetailedNewsDto(
            newsService.publishMessage(title, summary, text, image, eventId));
    }

    @PermitAll
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getNewsImage(@PathVariable Long id) {
        LOGGER.info("Fetching image for news id={}", id);
        return newsService.getNewsImage(id);
    }
}
