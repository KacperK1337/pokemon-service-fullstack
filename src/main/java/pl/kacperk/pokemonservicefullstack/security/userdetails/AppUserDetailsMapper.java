package pl.kacperk.pokemonservicefullstack.security.userdetails;

import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole;

public class AppUserDetailsMapper {

    public static AppUserDetails userToDetails(AppUser user) {
        final AppUserRole userRole = user.getRole();
        return AppUserDetails.builder()
            .authorities(userRole.getGrantedAuthorities())
            .username(user.getUserName())
            .password(user.getPassword())
            .isAccountNonExpired(user.isAccountNonExpired())
            .isAccountNonLocked(user.isAccountNonLocked())
            .isCredentialsNonExpired(user.isCredentialsNonExpired())
            .isEnabled(user.isEnabled())
            .build();
    }
    
}
