package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
