package pl.kacperk.pokemonservicefullstack.util.pokemonevolution;

public class EvolutionHandler {

    public static String getShortenedPokemonEvolutions(String evolutions) {
        if (evolutions != null) {
            int numberOfEvolutions =
                evolutions.length() - evolutions.replace("/", "").length() + 1;
            if (numberOfEvolutions > 2) {
                int ending = evolutions.indexOf("/", evolutions.indexOf("/") + 1);
                return evolutions.substring(0, ending + 1) + "...";
            }
        }
        return evolutions;
    }

}
