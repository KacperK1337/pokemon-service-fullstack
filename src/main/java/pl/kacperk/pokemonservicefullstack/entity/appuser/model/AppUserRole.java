package pl.kacperk.pokemonservicefullstack.entity.appuser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserPermission.POKEMONS_LIKE;

@AllArgsConstructor
@Getter
public enum AppUserRole {

    USER(Set.of(POKEMONS_LIKE));

    private final Set<AppUserPermission> permissions;

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        final Set<SimpleGrantedAuthority> authorities =
            getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(
                    permission.getPermission()
                ))
                .collect(toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
