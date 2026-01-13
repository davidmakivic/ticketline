package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findUserByEmail(String email);

    boolean existsByEmail(String email);

    ApplicationUser findUserByUserId(Long userId);

    List<ApplicationUser> findAllByUserStatus(UserStatus userStatus);

    Page<ApplicationUser> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    List<ApplicationUser> findAllByUserStatusNot(UserStatus userStatus);

    @Transactional
    @Modifying
    @Query("""
            UPDATE ApplicationUser u
            SET u.failedLoginAttempts = u.failedLoginAttempts + 1
            WHERE u.email = :email
        """)
    void incrementFailedLoginAttempts(@Param("email") String email);


    @Transactional
    @Modifying
    @Query("""
            UPDATE ApplicationUser u
            SET u.failedLoginAttempts = 0
            WHERE u.email = :email
        """)
    void setFailedLoginAttemptsToZero(@Param("email") String email);

    ApplicationUser getApplicationUserByEmail(String email);
}
