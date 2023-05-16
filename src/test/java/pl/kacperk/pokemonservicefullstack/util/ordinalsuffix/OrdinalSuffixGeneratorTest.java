package pl.kacperk.pokemonservicefullstack.util.ordinalsuffix;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.ND_SUFFIX;
import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.RD_SUFFIX;
import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.ST_SUFFIX;
import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.TH_SUFFIX;
import static pl.kacperk.pokemonservicefullstack.util.ordinalsuffix.OrdinalSuffixGenerator.getNumberWithSuffix;

class OrdinalSuffixGeneratorTest {

    @ParameterizedTest
    @ValueSource(longs = {1, 21, 101, 121})
    void getNumberWithSuffix_numberEndingWith1_stSuffix(final long number) {
        final var result = getNumberWithSuffix(number);

        assertThat(result)
            .isEqualTo(number + ST_SUFFIX);

    }

    @ParameterizedTest
    @ValueSource(longs = {2, 22, 102, 122})
    void getNumberWithSuffix_numberEndingWith2_ndSuffix(final long number) {
        final var result = getNumberWithSuffix(number);

        assertThat(result)
            .isEqualTo(number + ND_SUFFIX);
    }

    @ParameterizedTest
    @ValueSource(longs = {3, 23, 103, 123})
    void getNumberWithSuffix_numberEndingWith3_rdSuffix(final long number) {
        final var result = getNumberWithSuffix(number);

        assertThat(result)
            .isEqualTo(number + RD_SUFFIX);
    }

    @ParameterizedTest
    @ValueSource(longs = {11, 12, 13, 111, 112, 113})
    void getNumberWithSuffix_numberEndingWithTeen_thSuffix(final long number) {
        final var result = getNumberWithSuffix(number);

        assertThat(result)
            .isEqualTo(number + TH_SUFFIX);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 4, 10, 24, 100, 104, 124})
    void getNumberWithSuffix_numberWithoutRule_thSuffix(final long number) {
        final var result = getNumberWithSuffix(number);

        assertThat(result)
            .isEqualTo(number + TH_SUFFIX);
    }

}
