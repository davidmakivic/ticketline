package at.ac.tuwien.sepr.groupphase.backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class FileStorageUtil {

    private FileStorageUtil() {}

    public static String store(MultipartFile file, String targetDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path dir = Paths.get(targetDir);
        Files.createDirectories(dir);

        String original = file.getOriginalFilename();
        String ext = "";

        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }
        }

        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return filename; // store only filename in DB
    }
}

