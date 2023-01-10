package pl.kacperk.pokemonservicefullstack.api.appuser.model;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserPermission.POKEMONS_LIKE;

@AllArgsConstructor
@Getter
public enum AppUserRole {

    USER(Sets.newHashSet(POKEMONS_LIKE));

    private final Set<AppUserPermission> permissions;

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> authorities =
                getPermissions()
                        .stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                        .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
