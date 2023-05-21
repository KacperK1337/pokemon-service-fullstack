package pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.PokemonType;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;

public class PokemonResponseDtoMapper {

    protected static final String EVOLUTIONS_DELIMITER = " / ";
    protected static final String TYPES_DELIMITER = ", ";

    private static String getEvolutionNames(final Set<String> evolutions) {
        if (evolutions.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final String evolution : evolutions) {
            sb.append(evolution);
            sb.append(EVOLUTIONS_DELIMITER);
        }
        sb.setLength(sb.length() - EVOLUTIONS_DELIMITER.length());
        return sb.toString();
    }

    private static String getTypeNames(@NotEmpty final Set<PokemonType> types) {
        final StringBuilder sb = new StringBuilder();
        for (final PokemonType type : types) {
            sb.append(type);
            sb.append(TYPES_DELIMITER);
        }
        sb.setLength(sb.length() - TYPES_DELIMITER.length());
        return sb.toString();
    }

    public static PokemonResponseDto pokemonToResponseDto(final Pokemon pokemon) {
        final String pokemonTypeNames = getTypeNames(pokemon.getTypes());
        final String pokemonEvolutionNames = getEvolutionNames(pokemon.getEvolutions());
        return PokemonResponseDto.builder()
            .id(pokemon.getId())
            .name(pokemon.getName())
            .photoUrl(pokemon.getPhotoUrl())
            .evolutions(pokemonEvolutionNames)
            .typeNames(pokemonTypeNames)
            .numberOfLikes(pokemon.getNumberOfLikes())
            .build();
    }

    public static Set<PokemonResponseDto> pokemonsToResponseDtos(final Page<Pokemon> pokemons) {
        return pokemons.stream()
            .map(PokemonResponseDtoMapper::pokemonToResponseDto)
            .collect(toCollection(LinkedHashSet::new));
    }

}
