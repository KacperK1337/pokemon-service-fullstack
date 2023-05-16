package pl.kacperk.pokemonservicefullstack.util.pagenavigation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_NUM;
import static pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator.ALL_PAGES_LIMIT;
import static pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator.NEXT_PAGES_LIMIT;
import static pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator.PREVIOUS_PAGES_LIMIT;
import static pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator.getPageLimits;

class PageLimitsCalculatorTest {

    private static final int TOTAL_PAGES_OVER_ALL_PAGES_LIMIT = 20;

    static IntStream pageNumbersBeforeAllPagesLimit() {
        return IntStream.range(0, ALL_PAGES_LIMIT);
    }

    static IntStream pageNumbersToPreviousPagesLimit() {
        return IntStream.range(0, PREVIOUS_PAGES_LIMIT + 1);
    }

    static IntStream pageNumbersBetweenPagesLimits() {
        return IntStream.range(
            PREVIOUS_PAGES_LIMIT + 1, TOTAL_PAGES_OVER_ALL_PAGES_LIMIT - NEXT_PAGES_LIMIT
        );
    }

    static IntStream pageNumbersOverNextPagesLimit() {
        return IntStream.range(
            TOTAL_PAGES_OVER_ALL_PAGES_LIMIT - NEXT_PAGES_LIMIT, TOTAL_PAGES_OVER_ALL_PAGES_LIMIT
        );
    }

    @Test
    void getPageLimits_1Page_correctLimits() {
        final var pageLimits = getPageLimits(1, DEF_PAGE_NUM);

        assertThat(pageLimits)
            .containsOnly(0, 0);
    }

    @ParameterizedTest
    @MethodSource("pageNumbersBeforeAllPagesLimit")
    void getPageLimits_allPagesLimit_correctLimits(int pageNumber) {
        final var pageLimits = getPageLimits(ALL_PAGES_LIMIT, pageNumber);

        assertThat(pageLimits)
            .containsOnly(0, ALL_PAGES_LIMIT - 1);
    }

    @ParameterizedTest
    @MethodSource("pageNumbersToPreviousPagesLimit")
    void getPageLimits_PageNumberInPreviousPagesLimit_correctLimits(int pageNumber) {
        final var pageLimits = getPageLimits(TOTAL_PAGES_OVER_ALL_PAGES_LIMIT, pageNumber);

        assertThat(pageLimits)
            .containsOnly(0, ALL_PAGES_LIMIT - 1);
    }

    @ParameterizedTest
    @MethodSource("pageNumbersBetweenPagesLimits")
    void getPageLimits_PageNumberBetweenLimits_correctLimits(int pageNumber) {
        final var pageLimits = getPageLimits(TOTAL_PAGES_OVER_ALL_PAGES_LIMIT, pageNumber);

        assertThat(pageLimits)
            .containsOnly(pageNumber - PREVIOUS_PAGES_LIMIT, pageNumber + NEXT_PAGES_LIMIT);
    }

    @ParameterizedTest
    @MethodSource("pageNumbersOverNextPagesLimit")
    void getPageLimits_PageNumberInNextPagesLimit_correctLimits(int pageNumber) {
        final var pageLimits = getPageLimits(TOTAL_PAGES_OVER_ALL_PAGES_LIMIT, pageNumber);

        assertThat(pageLimits)
            .containsOnly(TOTAL_PAGES_OVER_ALL_PAGES_LIMIT - ALL_PAGES_LIMIT, TOTAL_PAGES_OVER_ALL_PAGES_LIMIT - 1);
    }

}
