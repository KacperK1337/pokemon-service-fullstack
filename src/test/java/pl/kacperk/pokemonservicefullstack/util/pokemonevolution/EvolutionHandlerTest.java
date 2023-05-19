package pl.kacperk.pokemonservicefullstack.util.pokemonevolution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.util.pokemonevolution.EvolutionHandler.getShortenedPokemonEvolutions;

class EvolutionHandlerTest {

    private static final String TEST_EVOLUTION = "testEvolution";
    private static final String TWO_EVOLUTIONS = TEST_EVOLUTION + "/" + TEST_EVOLUTION;
    private static final String THREE_EVOLUTIONS =
        TEST_EVOLUTION + "/" + TEST_EVOLUTION + "/" + TEST_EVOLUTION;
    private static final String EVOLUTIONS_SHORTENED =
        TEST_EVOLUTION + "/" + TEST_EVOLUTION + "/...";

    @ParameterizedTest
    @ValueSource(strings = {TEST_EVOLUTION, TWO_EVOLUTIONS})
    void getShortenedPokemonEvolutions_lessThan2Evolutions_notShortened(String evolutions) {
        final var shortenedEvolutions = getShortenedPokemonEvolutions(evolutions);

        assertThat(shortenedEvolutions)
            .isEqualTo(evolutions);
    }

    @Test
    void getShortenedPokemonEvolutions_3Evolutions_shortened() {
        final var shortenedEvolutions = getShortenedPokemonEvolutions(THREE_EVOLUTIONS);

        assertThat(shortenedEvolutions)
            .isEqualTo(EVOLUTIONS_SHORTENED);
    }

}
