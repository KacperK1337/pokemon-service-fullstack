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

    protected static final String USERNAME_NOT_FOUND_MESS = "Username %s not found";

    private final AppUserRepo appUserRepo;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final AppUser user = appUserRepo
                .findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USERNAME_NOT_FOUND_MESS, username))
                );
        return AppUserDetailsMapper.userToDetails(user);
    }

}
