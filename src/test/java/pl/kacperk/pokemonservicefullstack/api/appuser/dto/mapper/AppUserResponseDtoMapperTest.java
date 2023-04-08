package pl.kacperk.pokemonservicefullstack.api.appuser.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserResponseDtoMapperTest {

    private static final Long TEST_USER_ID = 2L;

    private AppUser testAppUser;

    @BeforeEach
    void setUp() {
        testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
    }

    @Test
    void appUserToAppUserResponseDto_appUserWithoutFavouritePokemon_correctAppUserResponse() {
        // given
        final var place = OrdinalSuffixGenerator.getNumberWithSuffix(
            testAppUser.getId() - 1
        );

        // when
        final var responseDto = AppUserResponseDtoMapper.appUserToAppUserResponseDto(testAppUser);

        // then
        assertThat(responseDto.getPlace())
            .isEqualTo(place);
        assertThat(responseDto.getUserName())
            .isEqualTo(testAppUser.getUserName());
        assertThat(responseDto.getFavouritePokemonName())
            .isNull();
    }

    @Test
    void appUserToAppUserResponseDto_appUserWithFavouritePokemon_correctAppUserResponse() {
        // given
        final var place = OrdinalSuffixGenerator.getNumberWithSuffix(
            testAppUser.getId() - 1
        );
        final var favouritePokemonName = "testFavouritePokemonName";
        testAppUser.setFavouritePokemonName(favouritePokemonName);

        // when
        final var responseDto = AppUserResponseDtoMapper.appUserToAppUserResponseDto(testAppUser);

        // then
        assertThat(responseDto.getPlace())
            .isEqualTo(place);
        assertThat(responseDto.getUserName())
            .isEqualTo(testAppUser.getUserName());
        assertThat(responseDto.getFavouritePokemonName())
            .isEqualTo(favouritePokemonName);
    }

}
