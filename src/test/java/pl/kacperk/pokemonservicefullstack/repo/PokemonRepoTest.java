package pl.kacperk.pokemonservicefullstack.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import pl.kacperk.pokemonservicefullstack.template.AbstractRepoTest;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.NON_EXISTING_POKEMON_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTIONS_NONE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_TYPES_1;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.createTestPokemon;

class PokemonRepoTest extends AbstractRepoTest {

    private static final Pokemon REPO_TEST_POKEMON = createTestPokemon(
        TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1
    );
    private static final PageRequest TEST_PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private PokemonRepo pokemonRepo;

    @BeforeEach
    void setUp() {
        pokemonRepo.deleteAll();
        pokemonRepo.save(REPO_TEST_POKEMON);
    }

    @Test
    void findByName_existingName_pokemonPresentEqual() {
        final var expectedPokemon = pokemonRepo.findByName(TEST_POKEMON_NAME);

        assertThat(expectedPokemon.isPresent())
            .isTrue();
    }

    @Test
    void findByName_nonExistingName_pokemonNotPresent() {
        final var expectedPokemon = pokemonRepo.findByName(NON_EXISTING_POKEMON_NAME);

        assertThat(expectedPokemon.isPresent())
            .isFalse();
    }

    @Test
    void findByNameContaining_matchFound_correctPage() {
        final var existingMatch = TEST_POKEMON_NAME.substring(0, 3);

        final var expectedPage = pokemonRepo.findByNameContaining(existingMatch, TEST_PAGEABLE);

        assertThat(expectedPage.getTotalElements())
            .isEqualTo(1);
    }

    @Test
    void findByNameContaining_matchNotFound_emptyPage() {
        final var expectedPage = pokemonRepo.findByNameContaining(NON_EXISTING_POKEMON_NAME, TEST_PAGEABLE);

        assertThat(expectedPage.isEmpty())
            .isTrue();
    }

}
