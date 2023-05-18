package pl.kacperk.pokemonservicefullstack.security.userdetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUser;
import static pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsMapper.appUserToAppUserDetails;

class AppUserDetailsMapperTest {

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestAppUser();
    }

    @Test
    void userToUserDetails_testUser_correctUserDetails() {
        final var details = appUserToAppUserDetails(testUser);

        assertThat(details.getAuthorities())
            .isEqualTo(
                testUser
                    .getRole()
                    .getGrantedAuthorities()
            );
        assertThat(details.getUsername())
            .isEqualTo(TEST_USER_NAME);
        assertThat(details.getPassword())
            .isEqualTo(TEST_USER_PASS);
        assertThat(true)
            .isEqualTo(details.isAccountNonExpired())
            .isEqualTo(details.isAccountNonLocked())
            .isEqualTo(details.isCredentialsNonExpired())
            .isEqualTo(details.isEnabled());
    }

    @Test
    void userToUserDetails_customUser_correctUserDetails() {
        testUser.setAccountNonExpired(false);
        final var details = appUserToAppUserDetails(testUser);

        assertThat(details.getAuthorities())
            .isEqualTo(
                testUser
                    .getRole()
                    .getGrantedAuthorities()
            );
        assertThat(details.getUsername())
            .isEqualTo(TEST_USER_NAME);
        assertThat(details.getPassword())
            .isEqualTo(TEST_USER_PASS);
        assertThat(details.isAccountNonExpired())
            .isFalse();
        assertThat(true)
            .isEqualTo(details.isAccountNonLocked())
            .isEqualTo(details.isCredentialsNonExpired())
            .isEqualTo(details.isEnabled());
    }

}
