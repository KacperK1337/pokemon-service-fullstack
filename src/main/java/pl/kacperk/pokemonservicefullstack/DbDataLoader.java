package pl.kacperk.pokemonservicefullstack;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;
import pl.kacperk.pokemonservicefullstack.api.pokemon.repo.PokemonRepo;
import pl.kacperk.pokemonservicefullstack.util.dbdataloader.DbDataLoaderUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DbDataLoader {
    private final AppUserRepo appUserRepo;
    private final PokemonRepo pokemonRepo;
    private final PasswordEncoder passwordEncoder;

    private void addUser(String role, String username, String pass) {
        AppUser appUser = new AppUser(AppUserRole.valueOf(role), username, passwordEncoder.encode(pass));
        appUserRepo.save(appUser);
    }

    private void addPokemon(String name, String evolutionNames, String typeNames, String photoUrl) {
        String formattedName = name.replace("_", " ");
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        if (!evolutionNames.equals("-")) {
            for (String evolutionName : evolutionNames.split("/")) {
                possibleEvolutions.add(evolutionName.replace("_", " "));
            }
        }
        Set<Type> types = new LinkedHashSet<>();
        for (String typeName : typeNames.split(",")) {
            types.add(Type.valueOf(typeName));
        }
        Pokemon pokemon = new Pokemon(formattedName, possibleEvolutions, types, photoUrl);
        pokemonRepo.save(pokemon);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        String line;
        String[] fields;
        try {
            // add appusers
            BufferedReader appUserReader =
                    DbDataLoaderUtils.createResourceBufferedReader("static/db/appusers_data.txt");
            while ((line = appUserReader.readLine()) != null) {
                fields = line.split(" ");
                addUser(fields[0], fields[1], fields[2]);
            }
            appUserReader.close();

            // add pokemons
            BufferedReader pokemonReader =
                    DbDataLoaderUtils.createResourceBufferedReader("static/db/pokemons_data.txt");
            while ((line = pokemonReader.readLine()) != null) {
                fields = line.split(" ");
                addPokemon(fields[0], fields[1], fields[2], fields[3]);
            }
            pokemonReader.close();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }
}
