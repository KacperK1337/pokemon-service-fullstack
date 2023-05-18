package pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PokemonResponseDto {

    private Long id;
    private String name;
    private String photoUrl;
    private String typeNames;
    private String possibleEvolutions;
    private int numberOfLikes;
}
