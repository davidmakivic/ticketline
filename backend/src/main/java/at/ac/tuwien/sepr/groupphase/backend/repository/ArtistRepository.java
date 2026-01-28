package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.repository.projection.ArtistImageProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT a FROM Artist a WHERE "
        + "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
        + "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
        + "LOWER(a.stageName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Artist> findByAnyName(@Param("name") String name);


    @Query("""
            SELECT a.id AS id, a.firstName AS firstName, a.lastName AS lastName, a.stageName AS stageName, a.artistType AS artistType
            FROM Artist a
            WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
               OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
               OR LOWER(a.stageName) LIKE LOWER(CONCAT('%', :name, '%'))
        """)
    List<ArtistAutocompleteDto> findArtistAutocompleteDto(@Param("name") String name, Pageable pageable);

    @Query("select a.imageContentType as imageContentType, a.imageData as imageData "
        + "from Artist a where a.id = :id")
    Optional<ArtistImageProjection> findImageById(@Param("id") Long id);

    @Query("select a.imageContentType from Artist a where a.id = :id")
    Optional<String> findImageContentTypeById(@Param("id") Long id);


}
