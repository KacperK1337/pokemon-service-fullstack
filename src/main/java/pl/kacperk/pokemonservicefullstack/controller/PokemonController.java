package pl.kacperk.pokemonservicefullstack.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response.PokemonResponseDto;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;

import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response.PokemonResponseDtoMapper.pokemonToPokemonResponseDto;
import static pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response.PokemonResponseDtoMapper.pokemonsToPokemonResponseDtos;
import static pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator.getPageLimits;

@Controller
@RequiredArgsConstructor
public class PokemonController {

    protected static final String POKEMONS_GET_POKEMON_MAPPING = "/api/pokemons/get/pokemon";
    protected static final String POKEMONS_GET_RANDOM_MAPPING = "/api/pokemons/get/random";
    protected static final String POKEMONS_GET_ALL_MAPPING = "/api/pokemons/get/all";
    protected static final String POKEMONS_GET_TOP_MAPPING = "/api/pokemons/get/top";
    protected static final String POKEMONS_LIKE_MAPPING = "/api/pokemons/like/{id}";

    protected static final String ID_REQUEST_PARAM = "id";
    protected static final String NAME_REQUEST_PARAM = "name";
    protected static final String PAGE_NUM_REQUEST_PARAM = "pageNum";
    protected static final String PAGE_SIZE_REQUEST_PARAM = "pageSize";
    protected static final String SORT_DIR_REQUEST_PARAM = "sortDir";
    protected static final String SORT_BY_REQUEST_PARAM = "sortBy";
    protected static final String MATCH_BY_REQUEST_PARAM = "matchBy";

    protected static final String PAGE_NUM_REQUEST_PARAM_DEF_VAL = "0";
    protected static final String PAGE_SIZE_REQUEST_PARAM_DEF_VAL = "20";
    protected static final String SORT_DIR_REQUEST_PARAM_DEF_VAL = "ASC";
    protected static final String SORT_BY_REQUEST_PARAM_DEF_VAL = "id";
    protected static final String MATCH_BY_REQUEST_PARAM_DEF_VAL = "";

    protected static final String EVOLUTIONS_SET_ATTR = "possibleEvolutionsSet";
    protected static final String EVOLUTIONS_ATTR = "possibleEvolutions";
    protected static final String POKEMON_ATTR = "pokemon";
    protected static final String PAGE_NUM_ATTR = "pageNum";
    protected static final String PAGE_SIZE_ATTR = "pageSize";
    protected static final String SORT_DIR_ATTR = "sortDir";
    protected static final String SORT_BY_ATTR = "sortBy";
    protected static final String MATCH_BY_ATTR = "matchBy";
    protected static final String POKEMONS_ATTR = "pokemons";
    protected static final String ALL_PAGES_ATTR = "allPages";
    protected static final String TOTAL_ELEMENTS_ATTR = "totalElements";
    protected static final String PAGE_LEFT_LIMIT_ATTR = "pageLeftLimit";
    protected static final String PAGE_RIGHT_LIMIT_ATTR = "pageRightLimit";
    protected static final String TOP_POKEMONS_ATTR = "topPokemons";

    protected static final String POKEMON_VIEW = "pokemon";
    protected static final String DATABASE_VIEW = "database";
    protected static final String RANKING_VIEW = "ranking";

    protected static final String POKEMON_FAVOURITE_URL = "/pokemon-favourite";

    protected static final String GET_POKEMON_NO_PARAMS_ERROR = "Pokemon can only be found by its id or name";

    private final PokemonService pokemonService;

    private Pokemon getPokemonByRequestParams(final Long id, final String name) {
        if (id != null) {
            return pokemonService.getPokemonById(id);
        } else {
            if (name != null) {
                return pokemonService.getPokemonByName(name);
            } else {
                throw new ResponseStatusException(
                    BAD_REQUEST, GET_POKEMON_NO_PARAMS_ERROR
                );
            }
        }
    }

    private void setPokemonModelAttributes(final Model model, final Pokemon pokemon) {
        final Set<String> evolutionsSet = pokemon.getPossibleEvolutions();
        model.addAttribute(EVOLUTIONS_SET_ATTR, evolutionsSet);
        model.addAttribute(EVOLUTIONS_ATTR, evolutionsSet.size());
        final PokemonResponseDto responseDto = pokemonToPokemonResponseDto(pokemon);
        model.addAttribute(POKEMON_ATTR, responseDto);
    }

    @GetMapping(POKEMONS_GET_POKEMON_MAPPING)
    public String getPokemon(
        final Model model,
        @RequestParam(
            value = ID_REQUEST_PARAM, required = false
        ) final Long id,
        @RequestParam(
            value = NAME_REQUEST_PARAM, required = false
        ) final String name
    ) {
        final Pokemon pokemon = getPokemonByRequestParams(id, name);
        setPokemonModelAttributes(model, pokemon);
        return POKEMON_VIEW;
    }

    @GetMapping(POKEMONS_GET_RANDOM_MAPPING)
    public String getRandomPokemon(final Model model) {
        final Pokemon randomPokemon = pokemonService.getRandomPokemon();
        setPokemonModelAttributes(model, randomPokemon);
        return POKEMON_VIEW;
    }

    // TODO: 17.05.2023 check if Object vars can be primitives here and beyond
    @GetMapping(POKEMONS_GET_ALL_MAPPING)
    public String getAllPokemons(
        final Model model,
        @RequestParam(
            value = PAGE_NUM_REQUEST_PARAM, required = false, defaultValue = PAGE_NUM_REQUEST_PARAM_DEF_VAL
        ) final Integer pageNumber,
        @RequestParam(
            value = PAGE_SIZE_REQUEST_PARAM, required = false, defaultValue = PAGE_SIZE_REQUEST_PARAM_DEF_VAL
        ) final Integer pageSize,
        @RequestParam(
            value = SORT_DIR_REQUEST_PARAM, required = false, defaultValue = SORT_DIR_REQUEST_PARAM_DEF_VAL
        ) final String sortDirectionName,
        @RequestParam(
            value = SORT_BY_REQUEST_PARAM, required = false, defaultValue = SORT_BY_REQUEST_PARAM_DEF_VAL
        ) final String fieldToSortBy,
        @RequestParam(
            value = MATCH_BY_REQUEST_PARAM, required = false, defaultValue = MATCH_BY_REQUEST_PARAM_DEF_VAL
        ) final String nameToMatch
    ) {
        model.addAttribute(PAGE_NUM_ATTR, pageNumber);
        model.addAttribute(PAGE_SIZE_ATTR, pageSize);
        model.addAttribute(SORT_DIR_ATTR, sortDirectionName);
        model.addAttribute(SORT_BY_ATTR, fieldToSortBy);
        model.addAttribute(MATCH_BY_ATTR, nameToMatch);

        final Page<Pokemon> pokemons = pokemonService.getAllPokemons(
            pageNumber, pageSize,
            sortDirectionName, fieldToSortBy,
            nameToMatch
        );
        final Set<PokemonResponseDto> responseDtos = pokemonsToPokemonResponseDtos(pokemons);
        model.addAttribute(POKEMONS_ATTR, responseDtos);
        final int allPages = pokemons.getTotalPages();
        final long totalElements = pokemons.getTotalElements();
        model.addAttribute(ALL_PAGES_ATTR, allPages);
        model.addAttribute(TOTAL_ELEMENTS_ATTR, totalElements);
        final int[] pageLimits = getPageLimits(allPages, pageNumber);
        model.addAttribute(PAGE_LEFT_LIMIT_ATTR, pageLimits[0]);
        model.addAttribute(PAGE_RIGHT_LIMIT_ATTR, pageLimits[1]);

        return DATABASE_VIEW;
    }

    @GetMapping(POKEMONS_GET_TOP_MAPPING)
    public String getTopPokemons(final Model model) {
        final Page<Pokemon> topPokemons = pokemonService.getTopPokemons();
        final Set<PokemonResponseDto> responseDtos = pokemonsToPokemonResponseDtos(topPokemons);
        model.addAttribute(TOP_POKEMONS_ATTR, responseDtos);

        return RANKING_VIEW;
    }

    @GetMapping(POKEMONS_LIKE_MAPPING)
    public String addPokemonToFavourites(
        @PathVariable final Long id,
        @AuthenticationPrincipal final AppUserDetails details
    ) {
        pokemonService.addPokemonToFavourites(id, details);
        return "redirect:" + POKEMON_FAVOURITE_URL;
    }

}
