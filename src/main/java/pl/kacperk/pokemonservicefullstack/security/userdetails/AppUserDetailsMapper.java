package pl.kacperk.pokemonservicefullstack.security.userdetails;

import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;

public class AppUserDetailsMapper {

    public static AppUserDetails appUserToAppUserDetails(AppUser appUser) {
        return AppUserDetails.builder()
                .authorities(appUser.getRole().getGrantedAuthorities())
                .username(appUser.getUserName())
                .password(appUser.getPassword())
                .isAccountNonExpired(appUser.isAccountNonExpired())
                .isAccountNonLocked(appUser.isAccountNonLocked())
                .isCredentialsNonExpired(appUser.isCredentialsNonExpired())
                .isEnabled(appUser.isEnabled())
                .build();
    }
}
