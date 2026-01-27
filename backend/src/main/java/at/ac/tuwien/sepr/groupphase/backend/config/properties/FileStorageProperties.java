package at.ac.tuwien.sepr.groupphase.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {

    private String baseStoragePath = "./uploads";
    private String newsImagePath = "./uploads/news-images";
    private String merchandiseImagePath = "./uploads/merchandise";

    public String getBaseStoragePath() {
        return baseStoragePath;
    }

    public void setBaseStoragePath(String baseStoragePath) {
        this.baseStoragePath = baseStoragePath;
    }

    public String getNewsImagePath() {
        return newsImagePath;
    }

    public void setNewsImagePath(String newsImagePath) {
        this.newsImagePath = newsImagePath;
    }

    public String getMerchandiseImagePath() {
        return merchandiseImagePath;
    }

    public void setMerchandiseImagePath(String merchandiseImagePath) {
        this.merchandiseImagePath = merchandiseImagePath;
    }
}
