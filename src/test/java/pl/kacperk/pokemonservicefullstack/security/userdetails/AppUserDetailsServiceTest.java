package pl.kacperk.pokemonservicefullstack.security.userdetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.kacperk.pokemonservicefullstack.template.AbstractMockitoTest;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUser;
import static pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsService.USERNAME_NOT_FOUND_MESS;

class AppUserDetailsServiceTest extends AbstractMockitoTest {

    private static final Class<UsernameNotFoundException> USERNAME_NOT_FOUND_EXCEPTION_CLASS =
        UsernameNotFoundException.class;

    @Mock
    private AppUserRepo userRepo;
    private AppUserDetailsService userDetailsService;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        userDetailsService = new AppUserDetailsService(userRepo);
        testUser = createTestAppUser();
    }

    @Test
    void loadUserByUsername_existingUsername_findByUserNameMethodInvoked() {
        given(userRepo.findByUserName(TEST_USER_NAME))
            .willReturn(Optional.of(testUser));

        userDetailsService.loadUserByUsername(TEST_USER_NAME);

        verify(userRepo)
            .findByUserName(TEST_USER_NAME);
    }

    @Test
    void loadUserByUsername_nonExistingUsername_throwUsernameNotFoundException() {
        given(userRepo.findByUserName(TEST_USER_NAME))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(TEST_USER_NAME))
            .isInstanceOf(USERNAME_NOT_FOUND_EXCEPTION_CLASS)
            .hasMessageContaining(
                String.format(USERNAME_NOT_FOUND_MESS, TEST_USER_NAME)
            );
    }

}
