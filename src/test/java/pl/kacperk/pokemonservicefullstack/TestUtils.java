package pl.kacperk.pokemonservicefullstack;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;

import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole.USER;
import static pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type.Grass;
import static pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type.Water;

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

    public static class ControllerUtils {

        public static final String REGISTERED_USER_NAME = "registeredUserName";
        public static final String REGISTERED_USER_PASS = "registeredUserPass";
        public static final AppUserRegisterRequestDto REGISTER_REQUEST_DTO = new AppUserRegisterRequestDto(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, REGISTERED_USER_PASS
        );

        public static final String PASS_PROP = "password";
        public static final String MATCHING_PASS_PROP = "matchingPassword";
        public static final String ID_PROP = "id";
        public static final String NAME_PROP = "name";
        public static final String EVOLUTIONS_PROP = "possibleEvolutions";
        public static final String PHOTO_URL_PROP = "photoUrl";

        public static final String LOGIN_URL = "http://localhost/auth/login";

        private static final String LOGIN_MAPPING = "/auth/login";
        private static final String USERNAME_PARAM = "username";
        private static final String PASS_PARAM = "password";
        private static final String SECURITY_CONTEXT_ATTR = "SPRING_SECURITY_CONTEXT";

        public static MockHttpSession getLoggedUserSession(
            final String registeredAppUserName, final String registeredAppUserPass,
            final MockMvc mockMvc
        ) throws Exception {
            final ResultActions resultLoginActions = mockMvc.perform(
                post(LOGIN_MAPPING)
                    .param(USERNAME_PARAM, registeredAppUserName)
                    .param(PASS_PARAM, registeredAppUserPass)
            );
            final SecurityContext securityContext = (SecurityContext) resultLoginActions
                .andReturn()
                .getRequest()
                .getSession()
                .getAttribute(SECURITY_CONTEXT_ATTR);
            final MockHttpSession sessionWithLoggedUser = new MockHttpSession();
            sessionWithLoggedUser.setAttribute(SECURITY_CONTEXT_ATTR, securityContext);
            return sessionWithLoggedUser;
        }

    }

    public static class ServiceUtils {

        public static final Class<ResponseStatusException> RESPONSE_STATUS_EXC_CLASS =
            ResponseStatusException.class;
        public static final String STATUS_PROP = "status";
        public static final HttpStatus NOT_FOUND_STATUS = NOT_FOUND;
        public static final HttpStatus UNAUTHORIZED_STATUS = UNAUTHORIZED;
        public static final String USER_NOT_LOGGED_MESS = "User is not logged in";

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
