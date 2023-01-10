package pl.kacperk.pokemonservicefullstack.api.appuser.dto.mapper;

import pl.kacperk.pokemonservicefullstack.api.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator;

public class AppUserResponseDtoMapper {

    public static AppUserResponseDto appUserToAppUserResponseDto(AppUser appUser) {
        String appUserPlace = OrdinalSuffixGenerator.getNumberWithSuffix(appUser.getId() - 1);
        return AppUserResponseDto.builder()
                .place(appUserPlace)
                .userName(appUser.getUserName())
                .favouritePokemonName(appUser.getFavouritePokemonName())
                .build();
    }
}
