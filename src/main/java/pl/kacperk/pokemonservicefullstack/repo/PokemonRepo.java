package pl.kacperk.pokemonservicefullstack.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;

import java.util.Optional;

@Repository
public interface PokemonRepo extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByName(final String name);

    Page<Pokemon> findByNameContaining(final String match, final Pageable pageable);

}
