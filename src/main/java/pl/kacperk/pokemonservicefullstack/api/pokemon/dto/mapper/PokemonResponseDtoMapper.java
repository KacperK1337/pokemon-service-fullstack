package pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper;

import org.springframework.data.domain.Page;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.response.PokemonResponseDto;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;

import java.util.LinkedHashSet;
import java.util.Set;

public class PokemonResponseDtoMapper {

    private static String getTypeNames(Set<Type> types) {
        StringBuilder sb = new StringBuilder();
        for (Type type : types) {
            sb.append(type);
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    private static String getEvolutionNames(Set<String> possibleEvolutions) {
        if (possibleEvolutions.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String evolution : possibleEvolutions) {
            sb.append(evolution);
            sb.append(" / ");
        }
        sb.setLength(sb.length() - 3);
        return sb.toString();
    }

    public static PokemonResponseDto pokemonToPokemonResponseDto(Pokemon pokemon) {
        String pokemonTypeNames = getTypeNames(pokemon.getTypes());
        String pokemonEvolutionNames = getEvolutionNames(pokemon.getPossibleEvolutions());
        return PokemonResponseDto.builder()
                .id(pokemon.getId())
                .name(pokemon.getName())
                .photoUrl(pokemon.getPhotoUrl())
                .typeNames(pokemonTypeNames)
                .possibleEvolutions(pokemonEvolutionNames)
                .numberOfLikes(pokemon.getNumberOfLikes())
                .build();
    }

    public static Set<PokemonResponseDto> pokemonsToPokemonResponseDtos(Page<Pokemon> pokemons) {
        Set<PokemonResponseDto> pokemonResponseDtos = new LinkedHashSet<>();
        for (Pokemon pokemon : pokemons) {
            pokemonResponseDtos.add(pokemonToPokemonResponseDto(pokemon));
        }
        return pokemonResponseDtos;
    }
}
