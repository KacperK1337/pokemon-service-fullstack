package pl.kacperk.pokemonservicefullstack.api.appuser.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kacperk.pokemonservicefullstack.ContainerTest;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AppUserRepoTest extends ContainerTest {

    @Autowired
    private AppUserRepo underTest;
    private AppUser testAppUser;

    @BeforeEach
    void setUp() {
        testAppUser = TestUtils.getTestAppUser();
        underTest.save(testAppUser);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByUserName_existingUserName_appUserPresentAndEqual() {
        //given
        final var userName = testAppUser.getUserName();

        // when
        final var expectedAppUser = underTest.findByUserName(userName);

        // then
        assertThat(expectedAppUser.isPresent())
                .isTrue();
        assertThat(expectedAppUser.get())
                .isEqualTo(testAppUser);
    }

    @Test
    void findByUserName_nonExistingUserName_appUserNotPresent() {
        // given
        final var userName = testAppUser.getUserName();

        // when
        final var expectedAppUser = underTest.findByUserName(
                userName.toUpperCase()
        );

        // then
        assertThat(expectedAppUser.isPresent())
                .isFalse();
    }

    @Test
    void countByRole_existingRole_correctResult() {
        // given
        final var role = AppUserRole.USER;

        // when
        final var expectedNumberOfAppUsers = underTest.countByRole(role);

        // then
        assertThat(expectedNumberOfAppUsers)
                .isEqualTo(1L);
    }

}
