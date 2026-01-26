package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.projection.MerchandiseImageProjection;
import at.ac.tuwien.sepr.groupphase.backend.repository.projection.NewsImageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAllByOrderByPublishedAtDesc();

    List<News> findByIdNotInOrderByPublishedAtDesc(List<Long> ids);

    List<News> findByIdInOrderByPublishedAtDesc(List<Long> ids);

    default List<News> findByIdNotInOrderByPublishedAtDescOrAll(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return findAllByOrderByPublishedAtDesc();
        }
        return findByIdNotInOrderByPublishedAtDesc(ids);
    }

    @Query("select a.imageContentType as imageContentType, a.imageData as imageData "
        + "from News a where a.id = :id")
    Optional<NewsImageProjection> findImageById(@Param("id") Long id);


    @Query("select a.imageContentType from News a where a.id = :id")
    Optional<String> findImageContentTypeById(@Param("id") Long id);

}
