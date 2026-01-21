package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ReadNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadNewsRepository extends JpaRepository<ReadNews, Long> {

    @Query("SELECT rn.news.id FROM ReadNews rn WHERE rn.user.userId = :userId")
    List<Long> findReadNewsIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(rn) > 0 THEN true ELSE false END FROM ReadNews rn WHERE rn.user.userId = :userId AND rn.news.id = :newsId")
    boolean existsByUserIdAndNewsId(@Param("userId") Long userId, @Param("newsId") Long newsId);
}
