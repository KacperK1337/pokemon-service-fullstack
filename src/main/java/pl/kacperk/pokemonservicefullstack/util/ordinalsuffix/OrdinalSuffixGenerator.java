package pl.kacperk.pokemonservicefullstack.util.ordinalsuffix;

public class OrdinalSuffixGenerator {

    protected static final String ST_SUFFIX = "st";
    protected static final String ND_SUFFIX = "nd";
    protected static final String RD_SUFFIX = "rd";
    protected static final String TH_SUFFIX = "th";

    public static String getNumberWithSuffix(final long number) {
        final long mod10 = number % 10;
        final long mod100 = number % 100;
        if (mod10 == 1 && mod100 != 11) {
            return number + ST_SUFFIX;
        } else if (mod10 == 2 && mod100 != 12) {
            return number + ND_SUFFIX;
        } else if (mod10 == 3 && mod100 != 13) {
            return number + RD_SUFFIX;
        } else {
            return number + TH_SUFFIX;
        }
    }

}
