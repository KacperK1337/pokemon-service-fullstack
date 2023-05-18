package pl.kacperk.pokemonservicefullstack;

import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Type;

import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole.USER;
import static pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Type.Grass;
import static pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Type.Water;

public class TestUtils {

    public static class UserUtils {

        public static final long TEST_USER_ID = 2;
        public static final String TEST_USER_NAME = "testUserName";
        public static final String TEST_USER_PASS = "testUserPass";
        public static final AppUserRole ROLE_USER = USER;

        public static AppUser createTestAppUser() {
            return new AppUser(ROLE_USER, TEST_USER_NAME, TEST_USER_PASS);
        }

        public static AppUser createTestAppUserWithId() {
            final AppUser testUser = createTestAppUser();
            testUser.setId(TEST_USER_ID);
            return testUser;
        }
    }

    public static class PokemonUtils {

        public static final long TEST_POKEMON_ID = 2;
        public static final long NON_EXISTING_POKEMON_ID = 10;

        public static final String TEST_POKEMON_NAME = "testPokemonName";
        public static final String NON_EXISTING_POKEMON_NAME = "nonExistingPokemonName";

        public static final Set<String> TEST_POKEMON_EVOLUTIONS_NONE = emptySet();
        public static final String TEST_POKEMON_EVOLUTION_1 = "testPokemonEvolution1";
        public static final String TEST_POKEMON_EVOLUTION_2 = "testPokemonEvolution2";
        public static final Set<String> TEST_POKEMON_EVOLUTIONS_1 = singleton(TEST_POKEMON_EVOLUTION_1);
        public static final Set<String> TEST_POKEMON_EVOLUTIONS_2 = Set.of(
            TEST_POKEMON_EVOLUTION_1, TEST_POKEMON_EVOLUTION_2
        );

        public static final Type TEST_POKEMON_TYPE_1 = Water;
        public static final Type TEST_POKEMON_TYPE_2 = Grass;
        public static final Set<Type> TEST_POKEMON_TYPES_1 = singleton(TEST_POKEMON_TYPE_1);
        public static final Set<Type> TEST_POKEMON_TYPES_2 = Set.of(
            TEST_POKEMON_TYPE_1, TEST_POKEMON_TYPE_2
        );

        public static final String TEST_POKEMON_PHOTO_URL = "testPokemonPhotoUrl";

        public static final int DEF_POKEMON_LIKES = 0;

        public static Pokemon createTestPokemon(
            final Set<String> testEvolutions, final Set<Type> testTypes
        ) {
            return new Pokemon(
                TEST_POKEMON_NAME, testEvolutions, testTypes, TEST_POKEMON_PHOTO_URL
            );
        }

        public static Pokemon createTestPokemonWithId(
            final Set<String> testEvolutions, final Set<Type> testTypes
        ) {
            final Pokemon testPokemon = createTestPokemon(testEvolutions, testTypes);
            testPokemon.setId(TEST_POKEMON_ID);
            return testPokemon;
        }
    }

    public static class PageableUtils {

        public static final int DEF_PAGE_NUM = 0;
        public static final int NON_DEF_PAGE_NUM = 1;
        public static final int DEF_PAGE_SIZE = 20;
        public static final int NON_DEF_PAGE_SIZE = 40;
        public static final String DEF_SORT = "ASC";
        public static final String NON_DEF_SORT = "DESC";
        public static final String DEF_FIELD_TO_SORT = "id";
        public static final String NON_DEF_FIELD_TO_SORT = "name";
        public static final String DEF_MATCH = "";
        public static final String NON_DEF_MATCH = "B";

    }

}
