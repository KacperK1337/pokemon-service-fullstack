package pl.kacperk.pokemonservicefullstack.api.appuser.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.google.common.collect.Iterables.getLast;
import static org.assertj.core.api.Assertions.assertThat;

class AppUserRoleTest {

    @Test
    void getGrantedAuthorities_roleUSER_correctGrantedAuthorities() {
        // given
        final var role = AppUserRole.USER;
        final var expectedRoleAuthority = new SimpleGrantedAuthority(
            "ROLE_" + role.name()
        );
        final var expectedPermissionAuthority = new SimpleGrantedAuthority(
            role.getPermissions()
                .stream()
                .iterator()
                .next()
                .getPermission()
        );

        // when
        final var authorities = role.getGrantedAuthorities();

        // then
        assertThat(authorities.size())
            .isEqualTo(2);
        assertThat(authorities.stream()
            .iterator()
            .next())
            .isEqualTo(expectedRoleAuthority);
        assertThat(getLast(authorities))
            .isEqualTo(expectedPermissionAuthority);
    }

}
