package pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.DEF_POKEMON_LIKES;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTIONS_1;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTIONS_2;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTIONS_NONE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTION_1;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTION_2;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_ID;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_PHOTO_URL;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_TYPES_1;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_TYPES_2;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_TYPE_1;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_TYPE_2;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.createTestPokemon;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.createTestPokemonWithId;
import static pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response.PokemonResponseDtoMapper.pokemonToPokemonResponseDto;
import static pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response.PokemonResponseDtoMapper.pokemonsToPokemonResponseDtos;

class PokemonResponseDtoMapperTest {

    private static final Set<String> TEST_POKEMON_EVOLUTIONS_2_NAMES_SET = Set.of(
        TEST_POKEMON_EVOLUTION_1 + " / " + TEST_POKEMON_EVOLUTION_2,
        TEST_POKEMON_EVOLUTION_2 + " / " + TEST_POKEMON_EVOLUTION_1
    );
    private static final String TEST_POKEMON_TYPE_1_NAME = TEST_POKEMON_TYPE_1.name();
    private static final String TEST_POKEMON_TYPE_2_NAME = TEST_POKEMON_TYPE_2.name();
    private static final Set<String> TEST_POKEMON_TYPES_2_NAMES_SET = Set.of(
        TEST_POKEMON_TYPE_1_NAME + ", " + TEST_POKEMON_TYPE_2_NAME,
        TEST_POKEMON_TYPE_2_NAME + ", " + TEST_POKEMON_TYPE_1_NAME
    );
    private static final String POKEMON_RESPONSE_DTO_EVOLUTIONS_FIELD = "possibleEvolutions";

    @Test
    void pokemonToPokemonResponseDto_pokemonWithoutEvolution_correctPokemonResponse() {
        final var testPokemon = createTestPokemonWithId(
            TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1
        );

        final var response = pokemonToPokemonResponseDto(testPokemon);

        assertThat(response.getId())
            .isEqualTo(TEST_POKEMON_ID);
        assertThat(response.getName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(response.getPossibleEvolutions())
            .isNull();
        assertThat(response.getTypeNames())
            .isEqualTo(TEST_POKEMON_TYPE_1_NAME);
        assertThat(response.getPhotoUrl())
            .isEqualTo(TEST_POKEMON_PHOTO_URL);
        assertThat(response.getNumberOfLikes())
            .isEqualTo(DEF_POKEMON_LIKES);
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith1PossibleEvolution_correctPokemonResponse() {
        final var testPokemon = createTestPokemonWithId(
            TEST_POKEMON_EVOLUTIONS_1, TEST_POKEMON_TYPES_1
        );

        final var response = pokemonToPokemonResponseDto(testPokemon);

        assertThat(response.getId())
            .isEqualTo(TEST_POKEMON_ID);
        assertThat(response.getName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(response.getPossibleEvolutions())
            .isEqualTo(TEST_POKEMON_EVOLUTION_1);
        assertThat(response.getTypeNames())
            .isEqualTo(TEST_POKEMON_TYPE_1_NAME);
        assertThat(response.getPhotoUrl())
            .isEqualTo(TEST_POKEMON_PHOTO_URL);
        assertThat(response.getNumberOfLikes())
            .isEqualTo(DEF_POKEMON_LIKES);
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith2PossibleEvolutions_correctPokemonResponse() {
        final var testPokemon = createTestPokemonWithId(
            TEST_POKEMON_EVOLUTIONS_2, TEST_POKEMON_TYPES_1
        );

        final var response = pokemonToPokemonResponseDto(testPokemon);

        assertThat(response.getId())
            .isEqualTo(TEST_POKEMON_ID);
        assertThat(response.getName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(response.getPossibleEvolutions())
            .isIn(TEST_POKEMON_EVOLUTIONS_2_NAMES_SET);
        assertThat(response.getTypeNames())
            .isEqualTo(TEST_POKEMON_TYPE_1_NAME);
        assertThat(response.getPhotoUrl())
            .isEqualTo(TEST_POKEMON_PHOTO_URL);
        assertThat(response.getNumberOfLikes())
            .isEqualTo(DEF_POKEMON_LIKES);
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith2Types_correctPokemonResponse() {
        final var testPokemon = createTestPokemonWithId(
            TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_2
        );

        final var response = pokemonToPokemonResponseDto(testPokemon);

        assertThat(response.getId())
            .isEqualTo(TEST_POKEMON_ID);
        assertThat(response.getName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(response.getPossibleEvolutions())
            .isNull();
        assertThat(response.getTypeNames())
            .isIn(TEST_POKEMON_TYPES_2_NAMES_SET);
        assertThat(response.getPhotoUrl())
            .isEqualTo(TEST_POKEMON_PHOTO_URL);
        assertThat(response.getNumberOfLikes())
            .isEqualTo(DEF_POKEMON_LIKES);
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith1Like_correctPokemonResponse() {
        final var testPokemon = createTestPokemonWithId(
            TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1
        );
        final var pokemonLikes = 1;
        testPokemon.setNumberOfLikes(pokemonLikes);

        final var response = pokemonToPokemonResponseDto(testPokemon);

        assertThat(response.getId())
            .isEqualTo(TEST_POKEMON_ID);
        assertThat(response.getName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(response.getPossibleEvolutions())
            .isNull();
        assertThat(response.getTypeNames())
            .isEqualTo(TEST_POKEMON_TYPE_1_NAME);
        assertThat(response.getPhotoUrl())
            .isEqualTo(TEST_POKEMON_PHOTO_URL);
        assertThat(response.getNumberOfLikes())
            .isEqualTo(pokemonLikes);
    }

    @Test
    void pokemonsToPokemonResponseDtos_variousPokemons_correctSetOfPokemons() {
        final var testPokemon1 = createTestPokemon(
            TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1
        );
        final var testPokemon2 = createTestPokemon(
            TEST_POKEMON_EVOLUTIONS_1, TEST_POKEMON_TYPES_1
        );
        final var testPokemonsPage = new PageImpl<>(List.of(
            testPokemon1, testPokemon2
        ));

        final var responseSet = pokemonsToPokemonResponseDtos(testPokemonsPage);

        assertThat(responseSet.size())
            .isEqualTo(2);
        assertThat(responseSet)
            .element(0)
            .hasFieldOrPropertyWithValue(POKEMON_RESPONSE_DTO_EVOLUTIONS_FIELD, null);
        assertThat(responseSet)
            .element(1)
            .hasFieldOrPropertyWithValue(
                POKEMON_RESPONSE_DTO_EVOLUTIONS_FIELD, TEST_POKEMON_EVOLUTION_1
            );
    }

}
