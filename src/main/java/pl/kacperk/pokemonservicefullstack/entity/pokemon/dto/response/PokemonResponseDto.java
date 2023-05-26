package pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PokemonResponseDto {

    private final Long id;
    private final String name;
    private final String photoUrl;
    private final String typeNames;
    private final String evolutions;
    private final int numberOfLikes;

}
