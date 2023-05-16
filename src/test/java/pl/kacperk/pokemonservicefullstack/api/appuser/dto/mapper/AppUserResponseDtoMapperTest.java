package pl.kacperk.pokemonservicefullstack.api.appuser.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_ID;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUserWithId;

class AppUserResponseDtoMapperTest {

    private static final long TEST_USER_PLACE = TEST_USER_ID - 1;
    private static final String USER_RESPONSE_DTO_PLACE_FIELD = "place";
    private static final String USER_RESPONSE_DTO_USER_NAME_FIELD = "userName";
    private static final String USER_RESPONSE_DTO_FAV_POKEMON_NAME_FIELD = "favouritePokemonName";

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestAppUserWithId();
    }

    @Test
    void userToUserResponseDto_userWithoutFavouritePokemon_correctUserResponse() {
        final var testUserPlace = OrdinalSuffixGenerator.getNumberWithSuffix(TEST_USER_PLACE);

        final var responseDto = AppUserResponseDtoMapper.appUserToAppUserResponseDto(testUser);

        assertThat(responseDto)
            .hasFieldOrPropertyWithValue(USER_RESPONSE_DTO_PLACE_FIELD, testUserPlace);
        assertThat(responseDto)
            .hasFieldOrPropertyWithValue(USER_RESPONSE_DTO_USER_NAME_FIELD, TEST_USER_NAME);
        assertThat(responseDto)
            .hasFieldOrPropertyWithValue(USER_RESPONSE_DTO_FAV_POKEMON_NAME_FIELD, null);
    }

    @Test
    void userToUserResponseDto_userWithFavouritePokemon_correctUserResponse() {
        final var testUserPlace = OrdinalSuffixGenerator.getNumberWithSuffix(TEST_USER_PLACE);
        testUser.setFavouritePokemonName(TEST_POKEMON_NAME);

        final var responseDto = AppUserResponseDtoMapper.appUserToAppUserResponseDto(testUser);

        assertThat(responseDto)
            .hasFieldOrPropertyWithValue(USER_RESPONSE_DTO_PLACE_FIELD, testUserPlace);
        assertThat(responseDto)
            .hasFieldOrPropertyWithValue(USER_RESPONSE_DTO_USER_NAME_FIELD, TEST_USER_NAME);
        assertThat(responseDto)
            .hasFieldOrPropertyWithValue(USER_RESPONSE_DTO_FAV_POKEMON_NAME_FIELD, TEST_POKEMON_NAME);
    }

}
