package pl.kacperk.pokemonservicefullstack.api.appuser.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AppUserRepoTest {

    @Autowired
    private AppUserRepo underTest;

    private AppUser testAppUser;

    private AppUser createTestAppUser() {
        return new AppUser(AppUserRole.USER, "testUserName", "testPassword");
    }

    @BeforeEach
    void setUp() {
        testAppUser = createTestAppUser();
        underTest.save(testAppUser);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByUserName_existingUserName_appUserPresentAndEqual() {
        //given
        String userName = testAppUser.getUserName();

        // when
        Optional<AppUser> expectedAppUser = underTest.findByUserName(userName);

        // then
        assertThat(expectedAppUser.isPresent()).isTrue();
        assertThat(expectedAppUser.get()).isEqualTo(testAppUser);
    }

    @Test
    void findByUserName_nonExistingUserName_appUserNotPresent() {
        // given
        String userName = testAppUser.getUserName();

        // when
        Optional<AppUser> expectedAppUser = underTest.findByUserName(userName.toUpperCase());

        // then
        assertThat(expectedAppUser.isPresent()).isFalse();
    }

    @Test
    void countByRole_existingRole_correctResult() {
        // given
        AppUserRole role = AppUserRole.USER;

        // when
        long expectedNumberOfAppUsers = underTest.countByRole(role);

        // then
        assertThat(expectedNumberOfAppUsers).isEqualTo(1L);
    }
}