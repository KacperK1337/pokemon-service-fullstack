package pl.kacperk.pokemonservicefullstack.util.pokemonevolution;

public class EvolutionHandler {

    protected static final String EVOLUTIONS_DELIMITER = "/";
    protected static final String THREE_DOTS = "...";

    public static String getShortenedPokemonEvolutions(final String evolutions) {
        if (evolutions != null) {
            final String evolutionsWithoutDelimiter = evolutions.replace(EVOLUTIONS_DELIMITER, "");
            final int numberOfEvolutions = evolutions.length() - evolutionsWithoutDelimiter.length() + 1;
            if (numberOfEvolutions > 2) {
                final int ending = evolutions.indexOf(
                    EVOLUTIONS_DELIMITER, evolutions.indexOf(EVOLUTIONS_DELIMITER) + 1
                );
                return evolutions.substring(0, ending + 1) + THREE_DOTS;
            }
        }
        return evolutions;
    }

}
