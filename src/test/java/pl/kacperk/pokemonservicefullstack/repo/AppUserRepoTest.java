package pl.kacperk.pokemonservicefullstack.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kacperk.pokemonservicefullstack.template.AbstractRepoTest;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.NON_EXISTING_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUser;
import static pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole.USER;

class AppUserRepoTest extends AbstractRepoTest {

    private static final AppUser REPO_TEST_USER = createTestAppUser();
    private static final long NUMBER_OF_USERS = 1;

    @Autowired
    private AppUserRepo userRepo;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        userRepo.save(REPO_TEST_USER);
    }

    @Test
    void findByUserName_existingUserName_userPresentAndEqual() {
        final var repoAppUser = userRepo.findByUserName(TEST_USER_NAME);

        assertThat(repoAppUser)
            .isPresent();
    }

    @Test
    void findByUserName_nonExistingUserName_userNotPresent() {
        final var repoAppUser = userRepo.findByUserName(NON_EXISTING_USER_NAME);

        assertThat(repoAppUser)
            .isNotPresent();
    }

    @Test
    void countByRole_existingRole_correctResult() {
        final var numberOfUsers = userRepo.countByRole(USER);

        assertThat(numberOfUsers)
            .isEqualTo(NUMBER_OF_USERS);
    }

}
