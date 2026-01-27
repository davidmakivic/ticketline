package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    @Operation(
        summary = "Get list of news with optional filters",
        description = "Returns a list of news entries, optionally filtered by read status. "
            + "When no status parameter is provided, returns all news. "
            + "When status=unread or status=read is provided, authentication is required and returns filtered news for the authenticated user. "
            + "Publicly accessible without filters.",
        parameters = {
            @Parameter(name = "status", description = "Filter by read status ('unread' or 'read') - requires authentication")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved news list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required for filtered news"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public List<SimpleNewsDto> findAll(@RequestParam(value = "status", required = false) String status) {
        LOGGER.info("GET /api/v1/news?status={}", status);

        if (status == null) {
            return newsMapper.newsToSimpleNewsDto(newsService.findAll());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required for filtered news");
        }

        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);

        if ("unread".equalsIgnoreCase(status)) {
            LOGGER.info("GET /api/v1/news?status=unread for user={}", email);
            return newsMapper.newsListToSimpleNewsDtoList(newsService.getUnreadNews(user.getUserId()));
        } else if ("read".equalsIgnoreCase(status)) {
            LOGGER.info("GET /api/v1/news?status=read for user={}", email);
            return newsMapper.newsToSimpleNewsDto(newsService.getReadNews(user.getUserId()));
        }

        return newsMapper.newsToSimpleNewsDto(newsService.findAll());
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    @Operation(
        summary = "Get detailed information about a specific news entry",
        description = "Returns comprehensive details about a news entry. "
            + "If user is authenticated, automatically marks the news as read for that user. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "News entry found"),
            @ApiResponse(responseCode = "404", description = "News entry not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public DetailedNewsDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        DetailedNewsDto news = newsMapper.newsToDetailedNewsDto(newsService.findOne(id));

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

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{id}/read-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Mark a news entry as read",
        description = "Marks a specific news entry as read for the authenticated user. "
            + "If already marked as read, this operation has no effect. "
            + "Requires USER or ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "204", description = "News marked as read successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - USER or ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "News entry not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public void updateReadStatus(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ApplicationUser user = userRepository.findUserByEmail(email);
        LOGGER.info("PUT /api/v1/news/{}/read-status for userId={}", id, user.getUserId());
        newsService.markAsRead(user.getUserId(), id);
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Create a new news entry",
        description = "Creates a new news entry with title, summary, text and optional image. "
            + "Can optionally be associated with an existing event. "
            + "Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "News entry created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Event not found (if eventId provided)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public DetailedNewsDto create(
        @RequestParam("title") String title,
        @RequestParam("summary") String summary,
        @RequestParam("text") String text,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "eventId", required = false) Long eventId) throws IOException {


        LOGGER.info("Creating new news: title={}, imagePresent={}, eventId={}",
            title, image != null && !image.isEmpty(), eventId);
        return newsMapper.newsToDetailedNewsDto(
            newsService.publishMessage(title, summary, text, image, eventId));
    }

    @PermitAll
    @GetMapping("/{id}/image")
    @Operation(
        summary = "Get image for a specific news entry",
        description = "Returns the image associated with a news entry in its original format. "
            + "Returns 204 No Content if no image is available. "
            + "Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No image available for this news entry"),
            @ApiResponse(responseCode = "404", description = "News entry not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<StreamingResponseBody> getNewsImage(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/news/{}/image", id);
        return newsService.streamNewsImage(id);
    }
}

