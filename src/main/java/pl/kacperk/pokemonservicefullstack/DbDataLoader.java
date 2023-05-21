package pl.kacperk.pokemonservicefullstack;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.PokemonType;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.repo.PokemonRepo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;
import static pl.kacperk.pokemonservicefullstack.util.dbdataloader.DbDataLoaderUtils.createResourceBufferedReader;

@Component
@RequiredArgsConstructor
public class DbDataLoader {

    private static final String USERS_DB_SOURCE = "static/db/appusers_data.txt";
    private static final String POKEMONS_DB_SOURCE = "static/db/pokemons_data.txt";

    private static final String RESOURCES_SPACE = "_";
    private static final String SPACE = " ";
    private static final String POKEMON_EMPTY_EVOLUTIONS_MARK = "-";
    private static final String POKEMON_EVOLUTIONS_DELIMITER = "/";
    private static final String POKEMON_TYPES_DELIMITER = ",";

    private final AppUserRepo appUserRepo;
    private final PokemonRepo pokemonRepo;
    private final PasswordEncoder passwordEncoder;

    private void addUser(
        final String role, final String userName, final String pass
    ) {
        final AppUserRole userRole = AppUserRole.valueOf(role);
        final String userPass = passwordEncoder.encode(pass);

        final AppUser user = new AppUser(
            userRole, userName, userPass
        );
        appUserRepo.save(user);
    }

    private void addPokemon(
        final String name, final String evolutionNames,
        final String typeNames, final String photoUrl
    ) {
        final String nameWithSpaces = name.replace(RESOURCES_SPACE, SPACE);

        final boolean emptyEvolutions = evolutionNames.equals(POKEMON_EMPTY_EVOLUTIONS_MARK);
        final Set<String> evolutions = emptyEvolutions ?
            stream(evolutionNames.split(POKEMON_EVOLUTIONS_DELIMITER))
                .map(evolutionName -> evolutionName.replace(RESOURCES_SPACE, SPACE))
                .collect(toCollection(LinkedHashSet::new))
            : emptySet();

        final Set<PokemonType> types =
            stream(typeNames.split(POKEMON_TYPES_DELIMITER))
                .map(PokemonType::valueOf)
                .collect(toCollection(LinkedHashSet::new));

        final Pokemon pokemon = new Pokemon(
            nameWithSpaces, evolutions,
            types, photoUrl
        );
        pokemonRepo.save(pokemon);
    }

    private void writeAppUsers(final BufferedReader appUserReader) {
        String line;
        try (appUserReader) {
            while (
                (line = appUserReader.readLine()) != null
            ) {
                final String[] fields = line.split(SPACE);
                addUser(fields[0], fields[1], fields[2]);
            }
        } catch (final IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private void writePokemons(final BufferedReader pokemonReader) {
        String line;
        try (pokemonReader) {
            while (
                (line = pokemonReader.readLine()) != null
            ) {
                final String[] fields = line.split(SPACE);
                addPokemon(fields[0], fields[1], fields[2], fields[3]);
            }
        } catch (final IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        writeAppUsers(
            createResourceBufferedReader(USERS_DB_SOURCE)
        );
        writePokemons(
            createResourceBufferedReader(POKEMONS_DB_SOURCE)
        );
    }

}
