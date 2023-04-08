package pl.kacperk.pokemonservicefullstack.userdetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private AppUserRepo appUserRepo;

    private AppUserDetailsService underTest;
    private AppUser testAppUser;

    private AppUser createTestAppUser() {
        return new AppUser(
                AppUserRole.USER, "testUserName", "testPassword"
        );
    }

    @BeforeEach
    void setUp() {
        underTest = new AppUserDetailsService(appUserRepo);
        testAppUser = createTestAppUser();
    }

    @Test
    void loadUserByUsername_existingUsername_findByUserNameMethodInvoked() {
        // given
        final var username = testAppUser.getUserName();

        given(appUserRepo.findByUserName(username))
                .willReturn(Optional.of(testAppUser));

        // when
        underTest.loadUserByUsername(username);

        // then
        verify(appUserRepo)
                .findByUserName(username);
    }

    @Test
    void loadUserByUsername_nonExistingUsername_throwUsernameNotFoundException() {
        // given
        final var username = testAppUser.getUserName();

        given(appUserRepo.findByUserName(username))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername(username))
                .isInstanceOf(
                        UsernameNotFoundException.class
                )
                .hasMessageContaining(
                        String.format("Username %s not found", username)
                );
    }

}
