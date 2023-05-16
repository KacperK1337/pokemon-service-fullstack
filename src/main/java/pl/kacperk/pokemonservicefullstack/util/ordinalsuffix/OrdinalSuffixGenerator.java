package pl.kacperk.pokemonservicefullstack.util.ordinalsuffix;

public class OrdinalSuffixGenerator {

    protected static final String ST_SUFFIX = "st";
    protected static final String ND_SUFFIX = "nd";
    protected static final String RD_SUFFIX = "rd";
    protected static final String TH_SUFFIX = "th";

    public static String getNumberWithSuffix(long number) {
        long j = number % 10;
        long k = number % 100;
        if (j == 1 && k != 11) {
            return number + ST_SUFFIX;
        } else if (j == 2 && k != 12) {
            return number + ND_SUFFIX;
        } else if (j == 3 && k != 13) {
            return number + RD_SUFFIX;
        } else {
            return number + TH_SUFFIX;
        }
    }
}
