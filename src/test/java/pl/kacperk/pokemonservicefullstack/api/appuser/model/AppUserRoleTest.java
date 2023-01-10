package pl.kacperk.pokemonservicefullstack.api.appuser.model;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserRoleTest {

    @Test
    void getGrantedAuthorities_roleUSER_correctGrantedAuthorities() {
        // given
        AppUserRole role = AppUserRole.USER;
        SimpleGrantedAuthority expectedRoleAuthority = new SimpleGrantedAuthority("ROLE_" + role.name());
        SimpleGrantedAuthority expectedPermissionAuthority = new SimpleGrantedAuthority(
                role.getPermissions()
                        .stream().iterator().next()
                        .getPermission()
        );

        // when
        Set<SimpleGrantedAuthority> authorities = role.getGrantedAuthorities();

        // then
        assertThat(authorities.size()).isEqualTo(2);
        assertThat(authorities.stream().iterator().next()).isEqualTo(expectedRoleAuthority);
        assertThat(Iterables.getLast(authorities)).isEqualTo(expectedPermissionAuthority);
    }
}