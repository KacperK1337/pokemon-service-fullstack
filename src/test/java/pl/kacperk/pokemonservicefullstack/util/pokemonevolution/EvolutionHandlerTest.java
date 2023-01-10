package pl.kacperk.pokemonservicefullstack.util.pokemonevolution;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvolutionHandlerTest {

    @Test
    void getShortenedPokemonEvolutions_nullEvolutions_nullResult() {
        // given
        String possibleEvolutions = null;

        // when
        String expectedShortenedEvolutions = EvolutionHandler.getShortenedPokemonEvolutions(possibleEvolutions);

        // then
        assertThat(expectedShortenedEvolutions).isNull();
    }

    @Test
    void getShortenedPokemonEvolutions_1PossibleEvolution_resultNotShortened() {
        // given
        String possibleEvolutions = "testName";

        // when
        String expectedShortenedEvolutions = EvolutionHandler.getShortenedPokemonEvolutions(possibleEvolutions);

        // then
        assertThat(expectedShortenedEvolutions).isEqualTo(possibleEvolutions);
    }

    @Test
    void getShortenedPokemonEvolutions_3PossibleEvolutions_resultShortened() {
        // given
        String testName = "testName";
        String possibleEvolutions = testName + "/" + testName + "/" + testName;

        // when
        String expectedShortenedEvolutions = EvolutionHandler.getShortenedPokemonEvolutions(possibleEvolutions);

        // then
        assertThat(expectedShortenedEvolutions).isEqualTo(testName + "/" + testName + "/...");
    }
}