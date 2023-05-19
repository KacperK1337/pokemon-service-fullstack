package pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response;

import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;

import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.getNumberWithSuffix;

public class AppUserResponseDtoMapper {

    public static AppUserResponseDto appUserToAppUserResponseDto(final AppUser appUser) {
        final String appUserPlace = getNumberWithSuffix(appUser.getId() - 1);
        return AppUserResponseDto.builder()
                .place(appUserPlace)
                .userName(appUser.getUserName())
                .favouritePokemonName(appUser.getFavouritePokemonName())
                .build();
    }

}
