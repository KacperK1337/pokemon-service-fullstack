package pl.kacperk.pokemonservicefullstack.userdetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsMapper.appUserToAppUserDetails;

class AppUserDetailsMapperTest {

    private AppUser testAppUser;

    private AppUser createTestAppUser() {
        return new AppUser(
                AppUserRole.USER, "testUserName", "testPassword"
        );
    }

    @BeforeEach
    void setUp() {
        testAppUser = createTestAppUser();
    }

    @Test
    void appUserToAppUserDetails_normalValues_correctAppUserDetails() {
        // when
        final var details = appUserToAppUserDetails(testAppUser);

        // then
        assertThat(details.getAuthorities())
                .isEqualTo(testAppUser
                        .getRole()
                        .getGrantedAuthorities());
        assertThat(details.getUsername())
                .isEqualTo(testAppUser.getUserName());
        assertThat(details.getPassword())
                .isEqualTo(testAppUser.getPassword());
        assertThat(true)
                .isEqualTo(details.isAccountNonExpired())
                .isEqualTo(details.isAccountNonLocked())
                .isEqualTo(details.isCredentialsNonExpired())
                .isEqualTo(details.isEnabled());
    }

}
