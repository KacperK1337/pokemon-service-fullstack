package pl.kacperk.pokemonservicefullstack.security.userdetails;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepo appUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepo
                .findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", username))
                );
        return AppUserDetailsMapper.appUserToAppUserDetails(appUser);
    }

}