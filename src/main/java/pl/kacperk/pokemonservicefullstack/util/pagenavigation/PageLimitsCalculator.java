package pl.kacperk.pokemonservicefullstack.util.pagenavigation;

public class PageLimitsCalculator {

    protected static final int PREVIOUS_PAGES_LIMIT = 11;
    protected static final int NEXT_PAGES_LIMIT = 3;
    protected static final int ALL_PAGES_LIMIT = PREVIOUS_PAGES_LIMIT + NEXT_PAGES_LIMIT + 1;

    public static int[] getPageLimits(final int totalPages, final int pageNumber) {
        int pageLeftLimit = 0;
        int pageRightLimit = totalPages - 1;

        if (totalPages > ALL_PAGES_LIMIT) {
            if (pageNumber <= PREVIOUS_PAGES_LIMIT) {
                pageRightLimit = ALL_PAGES_LIMIT - 1;
            } else if (pageNumber <= (totalPages - 1 - NEXT_PAGES_LIMIT)) {
                pageLeftLimit = pageNumber - PREVIOUS_PAGES_LIMIT;
                pageRightLimit = pageNumber + NEXT_PAGES_LIMIT;
            } else if (pageNumber > (totalPages - 1 - NEXT_PAGES_LIMIT)) {
                pageLeftLimit = totalPages - ALL_PAGES_LIMIT;
            }
        }

        return new int[]{pageLeftLimit, pageRightLimit};
    }

}
