package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.FileStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorageProperties props;

    public FileController(FileStorageProperties props) {
        this.props = props;
    }

    @PermitAll
    @GetMapping("/news-images/{filename:.+}")
    public ResponseEntity<Resource> getNewsImage(@PathVariable String filename) throws MalformedURLException {
        Path baseDir = Paths.get(props.getNewsImagePath()).toAbsolutePath().normalize();
        Path file = baseDir.resolve(filename).normalize();

        if (!file.startsWith(baseDir)) {
            return ResponseEntity.badRequest().build();
        }

        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());

        String contentType = "application/octet-stream";
        try {
            String probed = Files.probeContentType(file);
            if (probed != null) {
                contentType = probed;
            }
        } catch (IOException ignored) {
            // Use default content type
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }
}

