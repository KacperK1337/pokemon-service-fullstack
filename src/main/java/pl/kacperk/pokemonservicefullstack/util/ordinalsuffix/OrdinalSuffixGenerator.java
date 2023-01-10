package pl.kacperk.pokemonservicefullstack.util.ordinalsuffix;

public class OrdinalSuffixGenerator {

    public static String getNumberWithSuffix(long number) {
        long j = number % 10;
        long k = number % 100;
        if (j == 1 && k != 11) {
            return number + "st";
        } else if (j == 2 && k != 12) {
            return number + "nd";
        } else if (j == 3 && k != 13) {
            return number + "rd";
        } else {
            return number + "th";
        }
    }
}
