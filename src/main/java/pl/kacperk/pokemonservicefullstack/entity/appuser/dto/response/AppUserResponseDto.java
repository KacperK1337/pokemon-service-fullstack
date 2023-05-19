package pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppUserResponseDto {

    private final String place;
    private final String userName;
    private final String favouritePokemonName;

}
