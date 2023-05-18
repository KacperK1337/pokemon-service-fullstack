package pl.kacperk.pokemonservicefullstack.service;

import org.springframework.data.domain.Page;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;

public interface PokemonService {

    Pokemon getPokemonById(Long id);

    Pokemon getPokemonByName(String name);

    void addPokemonToFavourites(Long id, AppUserDetails details);

    Page<Pokemon> getAll(Integer pageNumber, Integer pageSize,
                         String sortDirectionName, String fieldToSortBy,
                         String nameToMach);

    Pokemon getTopPokemon();

    Pokemon getRandomPokemon();

}
