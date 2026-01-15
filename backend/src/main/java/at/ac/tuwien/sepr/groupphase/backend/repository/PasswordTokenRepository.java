package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByUser(ApplicationUser user);

    @Modifying
    @Query("""
            DELETE FROM PasswordResetToken t
            WHERE t.user.id = :userId
        """)
    void deleteTokenByUserId(@Param("userId") Long userId);

    PasswordResetToken findByToken(String token);
}
