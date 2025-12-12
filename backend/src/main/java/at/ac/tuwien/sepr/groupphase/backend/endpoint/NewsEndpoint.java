package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
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

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleNewsDto> findAll() {
        LOGGER.info("GET /api/v1/news");
        return newsMapper.newsToSimpleNewsDto(newsService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific news post", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        return newsMapper.newsToDetailedNewsDto(newsService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Publish a new news post", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto create(
        @RequestParam("title") String title,
        @RequestParam("summary") String summary,
        @RequestParam("text") String text,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        LOGGER.info("POST /api/v1/news");
        return newsMapper.newsToDetailedNewsDto(
            newsService.publishMessage(title, summary, text, image));
    }

    @PermitAll
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getNewsImage(@PathVariable Long id) {
        return newsService.getNewsImage(id);
    }
}
