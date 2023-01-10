package pl.kacperk.pokemonservicefullstack.api.appuser.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppUserResponseDto {

    private String place;
    private String userName;
    private String favouritePokemonName;

}
