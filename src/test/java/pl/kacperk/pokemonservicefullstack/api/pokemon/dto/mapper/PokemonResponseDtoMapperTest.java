package pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.getLast;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper.PokemonResponseDtoMapper.pokemonToPokemonResponseDto;
import static pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper.PokemonResponseDtoMapper.pokemonsToPokemonResponseDtos;

class PokemonResponseDtoMapperTest {

    private Pokemon testPokemon;

    private Pokemon createTestPokemon(final String name, final Set<String> possibleEvolutions, final Set<Type> types) {
        return new Pokemon(
                name, possibleEvolutions, types, "testPhotoUrl"
        );
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWithoutEvolution_correctPokemonResponse() {
        // given
        final Set<String> possibleEvolutions = new LinkedHashSet<>();
        final Set<Type> types = new LinkedHashSet<>();
        final var type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        final var response = pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId())
                .isEqualTo(testPokemon.getId());
        assertThat(response.getName())
                .isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl())
                .isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames())
                .isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions())
                .isNull();
        assertThat(response.getNumberOfLikes())
                .isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith1PossibleEvolution_correctPokemonResponse() {
        // given
        final Set<String> possibleEvolutions = new LinkedHashSet<>();
        final var possibleEvolution1 = "testEvolution1";
        possibleEvolutions.add(possibleEvolution1);
        final Set<Type> types = new LinkedHashSet<>();
        final var type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        final var response = pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId())
                .isEqualTo(testPokemon.getId());
        assertThat(response.getName())
                .isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl())
                .isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames())
                .isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions())
                .isEqualTo(possibleEvolution1);
        assertThat(response.getNumberOfLikes())
                .isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith2PossibleEvolutions_correctPokemonResponse() {
        // given
        final Set<String> possibleEvolutions = new LinkedHashSet<>();
        final var possibleEvolution1 = "testEvolution1";
        possibleEvolutions.add(possibleEvolution1);
        final var possibleEvolution2 = "testEvolution2";
        possibleEvolutions.add(possibleEvolution2);
        final Set<Type> types = new LinkedHashSet<>();
        final var type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        final var response = pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId())
                .isEqualTo(testPokemon.getId());
        assertThat(response.getName())
                .isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl())
                .isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames())
                .isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions())
                .isEqualTo(possibleEvolution1 + " / " + possibleEvolution2);
        assertThat(response.getNumberOfLikes())
                .isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith2Types_correctPokemonResponse() {
        // given
        final Set<String> possibleEvolutions = new LinkedHashSet<>();
        final Set<Type> types = new LinkedHashSet<>();
        final var type1 = Type.Water;
        types.add(type1);
        final var type2 = Type.Grass;
        types.add(type2);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        final var response = pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId())
                .isEqualTo(testPokemon.getId());
        assertThat(response.getName())
                .isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl())
                .isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames())
                .isEqualTo(type1.name() + ", " + type2.name());
        assertThat(response.getPossibleEvolutions())
                .isNull();
        assertThat(response.getNumberOfLikes())
                .isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith1Like_correctPokemonResponse() {
        // given
        final Set<String> possibleEvolutions = new LinkedHashSet<>();
        final Set<Type> types = new LinkedHashSet<>();
        final var type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);
        testPokemon.setNumberOfLikes(1);

        // when
        final var response = pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId())
                .isEqualTo(testPokemon.getId());
        assertThat(response.getName())
                .isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl())
                .isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames())
                .isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions())
                .isNull();
        assertThat(response.getNumberOfLikes())
                .isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonsToPokemonResponseDtos_variousPokemons_correctSetOfPokemons() {
        // given
        final Set<String> possibleEvolutions = new LinkedHashSet<>();
        final Set<Type> types = new LinkedHashSet<>();
        final var type1 = Type.Water;
        types.add(type1);

        final List<Pokemon> testPokemonsList = new ArrayList<>();
        final var testPokemon1 = createTestPokemon("testPokemon1", possibleEvolutions, types);
        testPokemonsList.add(testPokemon1);
        final var testPokemon2 = createTestPokemon("testPokemon2", possibleEvolutions, types);
        testPokemonsList.add(testPokemon2);
        final var testPokemonsPage = new PageImpl<>(testPokemonsList);

        // when
        final var responseSet = pokemonsToPokemonResponseDtos(testPokemonsPage);
        final var response1 = responseSet
                .iterator()
                .next();
        final var response2 = getLast(responseSet);

        // then
        assertThat(responseSet.size())
                .isEqualTo(2);

        assertThat(response1.getName())
                .isEqualTo(testPokemon1.getName());

        assertThat(response2.getName())
                .isEqualTo(testPokemon2.getName());
    }

}
