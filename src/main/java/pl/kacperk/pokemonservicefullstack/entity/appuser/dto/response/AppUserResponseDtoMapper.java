package pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response;

import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;

import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.getNumberWithSuffix;

public class AppUserResponseDtoMapper {

    public static AppUserResponseDto userToResponseDto(final AppUser user) {
        final String appUserPlace = getNumberWithSuffix(user.getId() - 1);
        return AppUserResponseDto.builder()
                .place(appUserPlace)
                .userName(user.getUserName())
                .favouritePokemonName(user.getFavouritePokemonName())
                .build();
    }

}
