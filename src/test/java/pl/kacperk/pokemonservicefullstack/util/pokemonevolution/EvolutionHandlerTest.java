package pl.kacperk.pokemonservicefullstack.util.pokemonevolution;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvolutionHandlerTest {

    @Test
    void getShortenedPokemonEvolutions_nullEvolutions_nullResult() {
        // when
        final var expectedShortenedEvolutions = EvolutionHandler.getShortenedPokemonEvolutions(null);

        // then
        assertThat(expectedShortenedEvolutions)
                .isNull();
    }

    @Test
    void getShortenedPokemonEvolutions_1PossibleEvolution_resultNotShortened() {
        // given
        final var possibleEvolutions = "testName";

        // when
        final var expectedShortenedEvolutions = EvolutionHandler.getShortenedPokemonEvolutions(possibleEvolutions);

        // then
        assertThat(expectedShortenedEvolutions)
                .isEqualTo(possibleEvolutions);
    }

    @Test
    void getShortenedPokemonEvolutions_3PossibleEvolutions_resultShortened() {
        // given
        final var testName = "testName";
        final var possibleEvolutions = testName + "/" + testName + "/" + testName;

        // when
        final var expectedShortenedEvolutions = EvolutionHandler.getShortenedPokemonEvolutions(possibleEvolutions);

        // then
        assertThat(expectedShortenedEvolutions)
                .isEqualTo(testName + "/" + testName + "/...");
    }

}
