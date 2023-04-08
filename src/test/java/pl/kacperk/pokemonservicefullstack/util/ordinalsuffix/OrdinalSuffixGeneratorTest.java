package pl.kacperk.pokemonservicefullstack.util.ordinalsuffix;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrdinalSuffixGeneratorTest {

    @ParameterizedTest
    @ValueSource(longs = {1, 21, 101, 121})
    void getNumberWithSuffix_numberEndingWith1_resultWithSt(final long number) {
        // when
        final var result = OrdinalSuffixGenerator.getNumberWithSuffix(number);

        // then
        assertThat(result)
                .isEqualTo(number + "st");

    }

    @ParameterizedTest
    @ValueSource(longs = {2, 22, 102, 122})
    void getNumberWithSuffix_numberEndingWith2_resultWithNd(final long number) {
        // when
        final var result = OrdinalSuffixGenerator.getNumberWithSuffix(number);

        // then
        assertThat(result)
                .isEqualTo(number + "nd");
    }

    @ParameterizedTest
    @ValueSource(longs = {3, 23, 103, 123})
    void getNumberWithSuffix_numberEndingWith3_resultWithRd(final long number) {
        // when
        final var result = OrdinalSuffixGenerator.getNumberWithSuffix(number);

        // then
        assertThat(result)
                .isEqualTo(number + "rd");
    }

    @ParameterizedTest
    @ValueSource(longs = {11, 12, 13, 111, 112, 113})
    void getNumberWithSuffix_numberEndingWithTeenNumberException_resultWithTh(final long number) {
        // when
        final var result = OrdinalSuffixGenerator.getNumberWithSuffix(number);

        // then
        assertThat(result)
                .isEqualTo(number + "th");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 4, 10, 24, 100, 104, 124})
    void getNumberWithSuffix_numberWithoutRule_numberWithTh(final long number) {
        // when
        final var result = OrdinalSuffixGenerator.getNumberWithSuffix(number);

        // then
        assertThat(result)
                .isEqualTo(number + "th");
    }

}
