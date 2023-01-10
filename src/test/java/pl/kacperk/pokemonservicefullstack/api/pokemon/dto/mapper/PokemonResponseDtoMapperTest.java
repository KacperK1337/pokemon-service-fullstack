package pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.response.PokemonResponseDto;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonResponseDtoMapperTest {

    private Pokemon testPokemon;

    private Pokemon createTestPokemon(String name, Set<String> possibleEvolutions, Set<Type> types) {
        return new Pokemon(name, possibleEvolutions, types, "testPhotoUrl");
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWithoutEvolution_correctPokemonResponse() {
        // given
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        Set<Type> types = new LinkedHashSet<>();
        Type type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        PokemonResponseDto response = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId()).isEqualTo(testPokemon.getId());
        assertThat(response.getName()).isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl()).isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames()).isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions()).isNull();
        assertThat(response.getNumberOfLikes()).isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith1PossibleEvolution_correctPokemonResponse() {
        // given
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        String possibleEvolution1 = "testEvolution1";
        possibleEvolutions.add(possibleEvolution1);
        Set<Type> types = new LinkedHashSet<>();
        Type type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        PokemonResponseDto response = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId()).isEqualTo(testPokemon.getId());
        assertThat(response.getName()).isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl()).isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames()).isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions()).isEqualTo(possibleEvolution1);
        assertThat(response.getNumberOfLikes()).isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith2PossibleEvolutions_correctPokemonResponse() {
        // given
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        String possibleEvolution1 = "testEvolution1";
        possibleEvolutions.add(possibleEvolution1);
        String possibleEvolution2 = "testEvolution2";
        possibleEvolutions.add(possibleEvolution2);
        Set<Type> types = new LinkedHashSet<>();
        Type type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        PokemonResponseDto response = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId()).isEqualTo(testPokemon.getId());
        assertThat(response.getName()).isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl()).isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames()).isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions()).isEqualTo(possibleEvolution1 + " / " + possibleEvolution2);
        assertThat(response.getNumberOfLikes()).isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith2Types_correctPokemonResponse() {
        // given
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        Set<Type> types = new LinkedHashSet<>();
        Type type1 = Type.Water;
        types.add(type1);
        Type type2 = Type.Grass;
        types.add(type2);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);

        // when
        PokemonResponseDto response = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId()).isEqualTo(testPokemon.getId());
        assertThat(response.getName()).isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl()).isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames()).isEqualTo(type1.name() + ", " + type2.name());
        assertThat(response.getPossibleEvolutions()).isNull();
        assertThat(response.getNumberOfLikes()).isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonToPokemonResponseDto_pokemonWith1Like_correctPokemonResponse() {
        // given
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        Set<Type> types = new LinkedHashSet<>();
        Type type1 = Type.Water;
        types.add(type1);
        testPokemon = createTestPokemon("testPokemon", possibleEvolutions, types);
        testPokemon.setNumberOfLikes(1);

        // when
        PokemonResponseDto response = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        // then
        assertThat(response.getId()).isEqualTo(testPokemon.getId());
        assertThat(response.getName()).isEqualTo(testPokemon.getName());
        assertThat(response.getPhotoUrl()).isEqualTo(testPokemon.getPhotoUrl());
        assertThat(response.getTypeNames()).isEqualTo(type1.name());
        assertThat(response.getPossibleEvolutions()).isNull();
        assertThat(response.getNumberOfLikes()).isEqualTo(testPokemon.getNumberOfLikes());
    }

    @Test
    void pokemonsToPokemonResponseDtos_variousPokemons_correctSetOfPokemons() {
        // given
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        Set<Type> types = new LinkedHashSet<>();
        Type type1 = Type.Water;
        types.add(type1);

        List<Pokemon> testPokemonsList = new ArrayList<>();
        Pokemon testPokemon1 = createTestPokemon("testPokemon1", possibleEvolutions, types);
        testPokemonsList.add(testPokemon1);
        Pokemon testPokemon2 = createTestPokemon("testPokemon2", possibleEvolutions, types);
        testPokemonsList.add(testPokemon2);
        Page<Pokemon> testPokemonsPage = new PageImpl<>(testPokemonsList);

        // when
        Set<PokemonResponseDto> responseSet =
                PokemonResponseDtoMapper.pokemonsToPokemonResponseDtos(testPokemonsPage);
        PokemonResponseDto response1 = responseSet.iterator().next();
        PokemonResponseDto response2 = Iterables.getLast(responseSet);

        // then
        assertThat(responseSet.size()).isEqualTo(2);

        assertThat(response1.getName()).isEqualTo(testPokemon1.getName());

        assertThat(response2.getName()).isEqualTo(testPokemon2.getName());
    }
}