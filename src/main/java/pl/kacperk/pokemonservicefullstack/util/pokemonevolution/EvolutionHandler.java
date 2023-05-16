package pl.kacperk.pokemonservicefullstack.util.pokemonevolution;

public class EvolutionHandler {

    public static String getShortenedPokemonEvolutions(String possibleEvolutions) {
        if (possibleEvolutions != null) {
            int numberOfPossibleEvolutions =
                possibleEvolutions.length() - possibleEvolutions.replace("/", "").length() + 1;
            if (numberOfPossibleEvolutions > 2) {
                int ending = possibleEvolutions.indexOf("/", possibleEvolutions.indexOf("/") + 1);
                return possibleEvolutions.substring(0, ending + 1) + "...";
            }
        }
        return possibleEvolutions;
    }

}
