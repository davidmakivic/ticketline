package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.projection.MerchandiseImageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long> {

    @Query("select a.imageContentType as imageContentType, a.imageData as imageData "
        + "from Merchandise a where a.id = :id")
    Optional<MerchandiseImageProjection> findImageById(@Param("id") Long id);


    @Query("select a.imageContentType from Merchandise a where a.id = :id")
    Optional<String> findImageContentTypeById(@Param("id") Long id);

}
