package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT a FROM Artist a WHERE "
        + "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
        + "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
        + "LOWER(a.stageName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Artist> findByAnyName(@Param("name") String name);
}
