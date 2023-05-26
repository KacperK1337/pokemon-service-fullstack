package pl.kacperk.pokemonservicefullstack.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.repo.PokemonRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.kacperk.pokemonservicefullstack.service.AppUserServiceImpl.USER_NOT_LOGGED_MESS;
import static pl.kacperk.pokemonservicefullstack.util.pageable.PageableCreator.getPageable;

@Service
@RequiredArgsConstructor
public class PokemonServiceImpl implements PokemonService {

    protected static final String POKEMON_NOT_FOUND_BY_ID_MESS = "Pokemon with id %s not found";
    protected static final String POKEMON_NOT_FOUND_BY_NAME_MESS = "Pokemon with name %s not found";
    protected static final String INVALID_REQUEST_PARAMS_MESS = "Invalid request parameters";

    protected static final int TOP_POKEMONS_PAGE_NUM = 0;
    protected static final int TOP_POKEMONS_PAGE_SIZE = 20;
    protected static final String TOP_POKEMONS_SORT_DIR = "DESC";
    protected static final String TOP_POKEMONS_SORT_FIELD = "numberOfLikes";
    protected static final String TOP_POKEMONS_MATCH_BY = "";

    protected static final long MIN_POKEMON_ID = 1;
    protected static final long MAX_POKEMON_ID = 905;

    private final PokemonRepo pokemonRepo;
    private final AppUserService appUserService;
    private final RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    @Override
    public Pokemon getPokemonById(final Long id) {
        return pokemonRepo
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                NOT_FOUND, String.format(POKEMON_NOT_FOUND_BY_ID_MESS, id)
            ));
    }

    @Override
    public Pokemon getPokemonByName(final String name) {
        return pokemonRepo
            .findByName(name)
            .orElseThrow(() -> new ResponseStatusException(
                NOT_FOUND, String.format(POKEMON_NOT_FOUND_BY_NAME_MESS, name)
            ));
    }

    @Transactional
    @Override
    public void addPokemonToFavourites(final Long id, final AppUserDetails details) {
        final AppUser loggedUser = appUserService.getLoggedUser(details);
        if (loggedUser == null) {
            throw new ResponseStatusException(
                UNAUTHORIZED, USER_NOT_LOGGED_MESS
            );
        }
        final Pokemon favPokemon = getPokemonById(id);
        final String userFavPokemonName = loggedUser.getFavouritePokemonName();
        if (userFavPokemonName != null) {
            getPokemonByName(userFavPokemonName).unlike();
        }
        loggedUser.setFavouritePokemonName(
            favPokemon.getName()
        );
        favPokemon.like();
    }

    @Override
    public Page<Pokemon> getAllPokemons(
        final int pageNumber, final int pageSize,
        final String sortDirectionName, final String fieldToSortBy,
        final String nameToMach
    ) {
        try {
            final Pageable requestPageable = getPageable(
                pageNumber, pageSize, sortDirectionName, fieldToSortBy
            );
            return pokemonRepo.findByNameContaining(nameToMach, requestPageable);
        } catch (final IllegalArgumentException iaEx) {
            throw new ResponseStatusException(
                BAD_REQUEST, INVALID_REQUEST_PARAMS_MESS
            );
        }
    }

    @Override
    public Page<Pokemon> getTopPokemons() {
        return getAllPokemons(
            TOP_POKEMONS_PAGE_NUM, TOP_POKEMONS_PAGE_SIZE,
            TOP_POKEMONS_SORT_DIR, TOP_POKEMONS_SORT_FIELD,
            TOP_POKEMONS_MATCH_BY
        );
    }

    @Override
    public Pokemon getTopPokemon() {
        return getTopPokemons()
            .iterator()
            .next();
    }

    @Override
    public Pokemon getRandomPokemon() {
        long randomId = randomDataGenerator.nextLong(MIN_POKEMON_ID, MAX_POKEMON_ID);
        return getPokemonById(randomId);
    }

}
