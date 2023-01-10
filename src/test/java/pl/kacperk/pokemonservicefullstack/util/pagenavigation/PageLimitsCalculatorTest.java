package pl.kacperk.pokemonservicefullstack.util.pagenavigation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageLimitsCalculatorTest {

    private final int prevPagesLimit = 11;
    private final int nextPagesLimit = 3;

    @Test
    void getPageLimits_1Page_correctLimits() {
        // given
        int allPages = 1;
        int pageNumber = 0;

        // when
        int[] pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0]).isEqualTo(0);
        assertThat(pageLimits[1]).isEqualTo(0);
    }

    @Test
    void getPageLimits_15Pages_correctLimits() {
        // given
        int allPages = 15;
        int pageNumber = 0;

        // when
        int[] pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0]).isEqualTo(0);
        assertThat(pageLimits[1]).isEqualTo(allPages - 1);
    }

    @Test
    void getPageLimits_16PagesWith11thPage_correctLimits() {
        // given
        int allPages = 16;
        int pageNumber = 11;

        // when
        int[] pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0]).isEqualTo(0);
        assertThat(pageLimits[1]).isEqualTo(pageNumber + nextPagesLimit);
    }

    @Test
    void getPageLimits_16PagesWith12thPage_correctLimits() {
        // given
        int allPages = 16;
        int pageNumber = 12;

        // when
        int[] pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0]).isEqualTo(pageNumber - prevPagesLimit);
        assertThat(pageLimits[1]).isEqualTo(pageNumber + nextPagesLimit);
    }

    @Test
    void getPageLimits_16PagesWith13thPage_correctLimits() {
        // given
        int allPages = 16;
        int pageNumber = 13;

        // when
        int[] pageLimits = PageLimitsCalculator.getPageLimits(allPages, pageNumber);

        // then
        assertThat(pageLimits[0]).isEqualTo(allPages - 1 - prevPagesLimit - nextPagesLimit);
        assertThat(pageLimits[1]).isEqualTo(allPages - 1);
    }
}