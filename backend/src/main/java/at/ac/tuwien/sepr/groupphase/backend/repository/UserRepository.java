package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findUserByEmail(String email);

    boolean existsByEmail(String email);

    ApplicationUser findUserByUserId(Long userId);

    List<ApplicationUser> findAllByUserStatus(UserStatus userStatus);

    List<ApplicationUser> findAllByUserStatusNot(UserStatus userStatus);
}
