package pl.kacperk.pokemonservicefullstack.util.pagenavigation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageLimitsCalculatorTest {

    private final int prevPagesLimit = 11;
    private final int nextPagesLimit = 3;

    @Test
    void getPageLimits_1Page_correctLimits() {
        // given
        final var allPages = 1;
        final var pageNumber = 0;

        // when
        final var pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0])
                .isEqualTo(0);
        assertThat(pageLimits[1])
                .isEqualTo(0);
    }

    @Test
    void getPageLimits_15Pages_correctLimits() {
        // given
        final var allPages = 15;
        final var pageNumber = 0;

        // when
        final var pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0])
                .isEqualTo(0);
        assertThat(pageLimits[1])
                .isEqualTo(allPages - 1);
    }

    @Test
    void getPageLimits_16Pages11thPage_correctLimits() {
        // given
        final var allPages = 16;
        final var pageNumber = 11;

        // when
        final var pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0])
                .isEqualTo(0);
        assertThat(pageLimits[1])
                .isEqualTo(pageNumber + nextPagesLimit);
    }

    @Test
    void getPageLimits_16Pages12thPage_correctLimits() {
        // given
        final var allPages = 16;
        final var pageNumber = 12;

        // when
        final var pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0])
                .isEqualTo(pageNumber - prevPagesLimit);
        assertThat(pageLimits[1])
                .isEqualTo(pageNumber + nextPagesLimit);
    }

    @Test
    void getPageLimits_16PagesWith13thPage_correctLimits() {
        // given
        final var allPages = 16;
        final var pageNumber = 13;

        // when
        final var pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0])
                .isEqualTo(allPages - 1 - prevPagesLimit - nextPagesLimit);
        assertThat(pageLimits[1])
                .isEqualTo(allPages - 1);
    }

}
