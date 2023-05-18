package pl.kacperk.pokemonservicefullstack.entity.appuser.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.ROLE_USER;

class AppUserRoleTest {

    @Test
    void getGrantedAuthorities_roleUSER_correctGrantedAuthorities() {
        final var defaultUserRoleAuthority = new SimpleGrantedAuthority("ROLE_" + ROLE_USER.name());
        final var defaultUserPermissionAuthority = new SimpleGrantedAuthority(
            ROLE_USER.getPermissions()
                .stream()
                .iterator()
                .next()
                .getPermission()
        );

        final var authorities = ROLE_USER.getGrantedAuthorities();

        assertThat(authorities)
            .containsOnly(defaultUserRoleAuthority, defaultUserPermissionAuthority);
    }

}
