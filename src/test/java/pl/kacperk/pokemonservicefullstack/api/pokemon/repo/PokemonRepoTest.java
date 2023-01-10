package pl.kacperk.pokemonservicefullstack.api.pokemon.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PokemonRepoTest {

    @Autowired
    private PokemonRepo underTest;

    private Pokemon testPokemon;

    private Pokemon createTestPokemon() {
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        possibleEvolutions.add("testEvolution");
        Set<Type> types = new LinkedHashSet<>();
        types.add(Type.Water);
        return new Pokemon("testName", possibleEvolutions, types, "testPhotoUrl");
    }

    @BeforeEach
    void setUp() {
        testPokemon = createTestPokemon();
        underTest.save(testPokemon);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_existingName_pokemonPresentAndEqual() {
        // when
        String name = testPokemon.getName();
        Optional<Pokemon> expectedPokemon = underTest.findByName(name);

        // then
        assertThat(expectedPokemon.isPresent()).isTrue();
        assertThat(expectedPokemon.get()).isEqualTo(testPokemon);
    }

    @Test
    void findByName_nonExistingName_pokemonNotPresent() {
        // given
        String name = testPokemon.getName();
        String nonExistingName = name.toUpperCase();

        // when
        Optional<Pokemon> expectedPokemon = underTest.findByName(nonExistingName);

        // then
        assertThat(expectedPokemon.isPresent()).isFalse();
    }

    @Test
    void findByNameContaining_matchFound_correctPage() {
        // given
        String name = testPokemon.getName();
        String existingMatch = name.substring(0, 3);
        Pageable testPageable = PageRequest.of(0, 10);

        // when
        Page<Pokemon> expectedPage = underTest.findByNameContaining(existingMatch, testPageable);

        // then
        assertThat(expectedPage.getTotalElements()).isEqualTo(1);
        assertThat(expectedPage.iterator().next()).isEqualTo(testPokemon);
    }

    @Test
    void findByNameContaining_matchNotFound_emptyPage() {
        // given
        String name = testPokemon.getName();
        String nonExistingMatch = name.toUpperCase();
        Pageable testPageable = PageRequest.of(0, 10);

        // when
        Page<Pokemon> expectedPage = underTest.findByNameContaining(nonExistingMatch, testPageable);

        // then
        assertThat(expectedPage.isEmpty()).isTrue();
    }
}