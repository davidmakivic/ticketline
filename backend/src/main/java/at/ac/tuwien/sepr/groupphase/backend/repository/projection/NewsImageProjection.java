package at.ac.tuwien.sepr.groupphase.backend.repository.projection;

import java.sql.Blob;

public interface NewsImageProjection {
    String getImageContentType();

    Blob getImageData();
}