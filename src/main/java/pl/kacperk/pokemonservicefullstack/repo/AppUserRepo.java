package pl.kacperk.pokemonservicefullstack.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole;

import java.util.Optional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUserName(String userName);

    long countByRole(AppUserRole role);

}
