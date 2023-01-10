package pl.kacperk.pokemonservicefullstack.util.pagenavigation;

public class PageLimitsCalculator {

    private static final int PREVIOUS_PAGES_LIMIT = 11;
    private static final int NEXT_PAGES_LIMIT = 3;

    public static int[] getPageLimits(int allPages, int pageNumber) {
        int pageLeftLimit = 0;
        int pageRightLimit = allPages - 1;
        if (allPages > 15) {
            if (pageNumber <= PREVIOUS_PAGES_LIMIT) {
                pageRightLimit = PREVIOUS_PAGES_LIMIT + NEXT_PAGES_LIMIT;
            } else if (pageNumber <= (allPages - 1 - NEXT_PAGES_LIMIT)) {
                pageLeftLimit = pageNumber - PREVIOUS_PAGES_LIMIT;
                pageRightLimit = pageNumber + NEXT_PAGES_LIMIT;
            } else if (pageNumber > (allPages - 1 - NEXT_PAGES_LIMIT)) {
                pageLeftLimit = allPages - 1 - PREVIOUS_PAGES_LIMIT - NEXT_PAGES_LIMIT;
            }
        }
        return new int[]{pageLeftLimit, pageRightLimit};
    }
}
