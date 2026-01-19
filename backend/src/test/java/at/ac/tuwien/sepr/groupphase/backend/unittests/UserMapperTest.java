package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void givenUser_whenMappedToDto_thenAllFieldsAreMapped() {
        ApplicationUser user = new ApplicationUser();
        user.setUserId(1L);
        user.setEmail("testuser@email.com");
        user.setPasswordHash("password1");
        user.setFirstName("first");
        user.setLastName("last");
        user.setCountry("Austria");
        user.setZipCode("1222");
        user.setCity("city");
        user.setStreet("street");
        user.setHouseNumber(12);
        user.setRole(Roles.USER);
        user.setRewardPoints(10);
        user.setUserStatus(UserStatus.UNVERIFIED);
        user.setFailedLoginAttempts(3);

        UserDetailDto dto = userMapper.applicationUserToUserDetailDto(user);

        assertAll(
            () -> assertEquals(1L, dto.getUserId()),
            () -> assertEquals("testuser@email.com", dto.getEmail()),
            () -> assertEquals("first", dto.getFirstName()),
            () -> assertEquals("last", dto.getLastName()),
            () -> assertEquals("Austria", dto.getCountry()),
            () -> assertEquals("1222", dto.getZipCode()),
            () -> assertEquals("city", dto.getCity()),
            () -> assertEquals("street", dto.getStreet()),
            () -> assertEquals(12, dto.getHouseNumber()),
            () -> assertEquals(Roles.USER, dto.getRole()),
            () -> assertEquals(10, dto.getRewardPoints()),
            () -> assertEquals(UserStatus.UNVERIFIED, dto.getUserStatus()),
            () -> assertEquals(3, dto.getFailedLoginAttempts())
        );
    }
}
