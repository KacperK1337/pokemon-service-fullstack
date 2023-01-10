package pl.kacperk.pokemonservicefullstack.api.pokemon.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.repo.PokemonRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.pageable.PageableCreator;

@Service
@RequiredArgsConstructor
public class PokemonServiceImpl implements PokemonService {

    private final PokemonRepo pokemonRepo;
    private final AppUserService appUserService;

    @Override
    public Pokemon getPokemonById(Long id) {
        return pokemonRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Pokemon with id %s not found", id)
                ));
    }

    @Override
    public Pokemon getPokemonByName(String name) {
        return pokemonRepo.findByName(name).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Pokemon with name %s not found", name))
        );
    }

    @Transactional
    @Override
    public void addPokemonToFavourites(Long id, AppUserDetails details) {
        AppUser loggedUser = appUserService.getLoggedAppUser(details);
        if (loggedUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User is not logged in"
            );
        }
        Pokemon favouritePokemon = getPokemonById(id);
        String userFavouritePokemonName = loggedUser.getFavouritePokemonName();
        if (userFavouritePokemonName != null) {
            getPokemonByName(userFavouritePokemonName).unlike();
        }
        loggedUser.setFavouritePokemonName(favouritePokemon.getName());
        favouritePokemon.like();
    }

    @Override
    public Page<Pokemon> getAll(Integer pageNumber, Integer pageSize,
                                String sortDirectionName, String fieldToSortBy,
                                String nameToMach) {
        try {
            Pageable requestedPageable =
                    PageableCreator.getPageable(pageNumber, pageSize, sortDirectionName, fieldToSortBy);
            return pokemonRepo.findByNameContaining(nameToMach, requestedPageable);
        } catch (IllegalArgumentException iaEx) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid request parameters"
            );
        }
    }

    @Override
    public Pokemon getTopPokemon() {
        return getAll(0, 1, "DESC", "numberOfLikes", "")
                .iterator()
                .next();
    }

    @Override
    public Pokemon getRandomPokemon() {
        long randomId = new RandomDataGenerator().nextLong(1L, 905L);
        return getPokemonById(randomId);
    }

}
