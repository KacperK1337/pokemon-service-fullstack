package pl.kacperk.pokemonservicefullstack.api.pokemon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper.PokemonResponseDtoMapper;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.response.PokemonResponseDto;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator;

import java.util.Set;

@Controller
@RequestMapping("/api/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final PokemonService pokemonService;

    @GetMapping("/get/pokemon")
    public String getPokemon(
            final Model model,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "name", required = false) String name
    ) {
        Pokemon pokemon;
        if (id != null) {
            pokemon = pokemonService.getPokemonById(id);
        } else {
            if (name != null) {
                pokemon = pokemonService.getPokemonByName(name);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Pokemon can only be found by its id or name"
                );
            }
        }
        Set<String> possibleEvolutionsSet = pokemon.getPossibleEvolutions();
        model.addAttribute("possibleEvolutionsSet", possibleEvolutionsSet);
        model.addAttribute("possibleEvolutions", possibleEvolutionsSet.size());
        PokemonResponseDto responseDto = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(pokemon);
        model.addAttribute("pokemon", responseDto);
        return "pokemon";
    }

    @GetMapping("/get/random")
    public String getRandomPokemon(final Model model) {
        Pokemon randomPokemon = pokemonService.getRandomPokemon();
        model.addAttribute("possibleEvolutions", randomPokemon.getPossibleEvolutions().size());
        model.addAttribute("possibleEvolutionsSet", randomPokemon.getPossibleEvolutions());
        PokemonResponseDto responseDto = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(randomPokemon);
        model.addAttribute("pokemon", responseDto);
        return "pokemon";
    }

    @GetMapping("/get/all")
    public String getAllPokemons(
            final Model model,
            @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "sortDir", required = false, defaultValue = "ASC") String sortDirectionName,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String fieldToSortBy,
            @RequestParam(value = "matchBy", required = false, defaultValue = "") String nameToMatch
    ) {
        model.addAttribute("pageNum", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortDir", sortDirectionName);
        model.addAttribute("sortBy", fieldToSortBy);
        model.addAttribute("matchBy", nameToMatch);
        Page<Pokemon> pokemons =
                pokemonService.getAll(pageNumber, pageSize, sortDirectionName, fieldToSortBy, nameToMatch);
        Set<PokemonResponseDto> responseDtos = PokemonResponseDtoMapper.pokemonsToPokemonResponseDtos(pokemons);
        model.addAttribute("pokemons", responseDtos);
        int allPages = pokemons.getTotalPages();
        long totalElements = pokemons.getTotalElements();
        model.addAttribute("allPages", allPages);
        model.addAttribute("totalElements", totalElements);
        int[] pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);
        model.addAttribute("pageLeftLimit", pageLimits[0]);
        model.addAttribute("pageRightLimit", pageLimits[1]);
        return "database";
    }

    @GetMapping("/get/top")
    public String getTopPokemons(final Model model) {
        Page<Pokemon> topPokemons = pokemonService.getAll(0, 20, "DESC", "numberOfLikes", "");
        Set<PokemonResponseDto> responseDtos = PokemonResponseDtoMapper.pokemonsToPokemonResponseDtos(topPokemons);
        model.addAttribute("topPokemons", responseDtos);
        return "ranking";
    }

    @GetMapping("/like/{id}")
    public String addPokemonToFavourites(
            @PathVariable final Long id,
            @AuthenticationPrincipal AppUserDetails details
    ) {
        pokemonService.addPokemonToFavourites(id, details);
        return "redirect:/pokemon-favourite";
    }
}
