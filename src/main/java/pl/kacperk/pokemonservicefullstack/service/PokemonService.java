package pl.kacperk.pokemonservicefullstack.service;

import org.springframework.data.domain.Page;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;

public interface PokemonService {

    Pokemon getPokemonById(final Long id);

    Pokemon getPokemonByName(final String name);

    void addPokemonToFavourites(final Long id, final AppUserDetails userDetails);

    Page<Pokemon> getAllPokemons(
        final int pageNumber, final int pageSize,
        final String sortDirectionName, final String fieldToSortBy,
        final String nameToMach
    );

    Page<Pokemon> getTopPokemons();

    Pokemon getTopPokemon();

    Pokemon getRandomPokemon();

}
