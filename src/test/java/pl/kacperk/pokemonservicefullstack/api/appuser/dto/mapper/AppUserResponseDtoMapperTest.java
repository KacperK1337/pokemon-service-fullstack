package pl.kacperk.pokemonservicefullstack.api.appuser.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserResponseDtoMapperTest {

    private AppUser testAppUser;

    private AppUser createTestAppUser() {
        return new AppUser(AppUserRole.USER, "testUserName", "testPassword");
    }

    @BeforeEach
    void setUp() {
        testAppUser = createTestAppUser();
    }

    @Test
    void appUserToAppUserResponseDto_appUserWithoutFavouritePokemon_correctAppUserResponse() {
        // given
        testAppUser.setId(2L);
        String place = OrdinalSuffixGenerator.getNumberWithSuffix(testAppUser.getId() - 1);

        // when
        AppUserResponseDto responseDto = AppUserResponseDtoMapper.appUserToAppUserResponseDto(testAppUser);

        // then
        assertThat(responseDto.getPlace()).isEqualTo(place);
        assertThat(responseDto.getUserName()).isEqualTo(testAppUser.getUserName());
        assertThat(responseDto.getFavouritePokemonName()).isNull();
    }

    @Test
    void appUserToAppUserResponseDto_appUserWithFavouritePokemon_correctAppUserResponse() {
        // given
        testAppUser.setId(2L);
        String place = OrdinalSuffixGenerator.getNumberWithSuffix(testAppUser.getId() - 1);
        String favouritePokemonName = "testFavouritePokemonName";
        testAppUser.setFavouritePokemonName(favouritePokemonName);

        // when
        AppUserResponseDto responseDto = AppUserResponseDtoMapper.appUserToAppUserResponseDto(testAppUser);

        // then
        assertThat(responseDto.getPlace()).isEqualTo(place);
        assertThat(responseDto.getUserName()).isEqualTo(testAppUser.getUserName());
        assertThat(responseDto.getFavouritePokemonName()).isEqualTo(favouritePokemonName);
    }
}