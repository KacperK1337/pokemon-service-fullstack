package pl.kacperk.pokemonservicefullstack.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.kacperk.pokemonservicefullstack.AbstractMockitoTest;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.repo.PokemonRepo;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.NON_EXISTING_POKEMON_ID;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.NON_EXISTING_POKEMON_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_EVOLUTIONS_NONE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_ID;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_TYPES_1;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.createTestPokemon;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.createTestPokemonWithId;
import static pl.kacperk.pokemonservicefullstack.service.AppUserServiceImpl.USER_NOT_LOGGED_MESS;
import static pl.kacperk.pokemonservicefullstack.service.PokemonServiceImpl.INVALID_REQUEST_PARAMS_MESS;
import static pl.kacperk.pokemonservicefullstack.service.PokemonServiceImpl.POKEMON_NOT_FOUND_BY_ID_MESS;
import static pl.kacperk.pokemonservicefullstack.service.PokemonServiceImpl.POKEMON_NOT_FOUND_BY_NAME_MESS;
import static pl.kacperk.pokemonservicefullstack.service.ServiceTestUtils.RESPONSE_STATUS_EXC_CLASS;
import static pl.kacperk.pokemonservicefullstack.service.ServiceTestUtils.STATUS_PROP;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUser;
import static pl.kacperk.pokemonservicefullstack.util.pageable.PageableCreator.getPageable;

class PokemonServiceImplTest extends AbstractMockitoTest {

    private static final AppUser TEST_USER = createTestAppUser();
    private static final String INVALID_SORT = "invalidSort";

    @Mock
    private PokemonRepo pokemonRepo;
    @Mock
    private AppUserService userService;
    private PokemonServiceImpl pokemonServiceImpl;
    private Pokemon testPokemon;

    @BeforeEach
    void setUp() {
        pokemonServiceImpl = new PokemonServiceImpl(pokemonRepo, userService);
        testPokemon = createTestPokemonWithId(
            TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1
        );
    }

    @Test
    void getPokemonById_existingId_findByIdMethodInvoked() {
        given(pokemonRepo.findById(TEST_POKEMON_ID))
            .willReturn(Optional.of(testPokemon));

        pokemonServiceImpl.getPokemonById(TEST_POKEMON_ID);

        verify(pokemonRepo)
            .findById(TEST_POKEMON_ID);
    }

    @Test
    void getPokemonById_nonExistingId_throwResponseStatusException() {
        given(pokemonRepo.findById(NON_EXISTING_POKEMON_ID))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> pokemonServiceImpl.getPokemonById(NON_EXISTING_POKEMON_ID))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, NOT_FOUND)
            .hasMessageContaining(
                String.format(POKEMON_NOT_FOUND_BY_ID_MESS, NON_EXISTING_POKEMON_ID)
            );
    }

    @Test
    void getPokemonByName_existingName_findByNameMethodInvoked() {
        given(pokemonRepo.findByName(TEST_POKEMON_NAME))
            .willReturn(Optional.of(testPokemon));

        pokemonServiceImpl.getPokemonByName(TEST_POKEMON_NAME);

        verify(pokemonRepo)
            .findByName(TEST_POKEMON_NAME);
    }

    @Test
    void getPokemonByName_nonExistingName_throwResponseStatusException() {
        given(pokemonRepo.findByName(NON_EXISTING_POKEMON_NAME))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> pokemonServiceImpl.getPokemonByName(NON_EXISTING_POKEMON_NAME))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, NOT_FOUND)
            .hasMessageContaining(
                String.format(POKEMON_NOT_FOUND_BY_NAME_MESS, NON_EXISTING_POKEMON_NAME)
            );
    }

    @Test
    void addPokemonToFavourites_loggedUserWithoutFavouritePokemon_loggedUserWithFavouritePokemonOf1Like() {
        final var loggedTestUser = TEST_USER;
        given(userService.getLoggedUser(any()))
            .willReturn(loggedTestUser);
        given(pokemonRepo.findById(TEST_POKEMON_ID))
            .willReturn(Optional.of(testPokemon));

        pokemonServiceImpl.addPokemonToFavourites(TEST_POKEMON_ID, any());

        verify(pokemonRepo, never())
            .findByName(any());
        assertThat(loggedTestUser.getFavouritePokemonName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(testPokemon.getNumberOfLikes())
            .isEqualTo(1);
    }

    @Test
    @Transactional
    void addPokemonToFavourites_loggedUserWithFavouritePokemon_LoggedUserWithNewFavouritePokemonOf1Like() {
        final var loggedTestUser = TEST_USER;
        final var testFavouritePokemonName = "favouritePokemonName";
        final var testFavouritePokemon = createTestPokemon(
            TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1
        );
        testFavouritePokemon.setName(testFavouritePokemonName);
        testFavouritePokemon.setNumberOfLikes(1);
        loggedTestUser.setFavouritePokemonName(testFavouritePokemonName);
        given(userService.getLoggedUser(any()))
            .willReturn(loggedTestUser);
        given(pokemonRepo.findById(TEST_POKEMON_ID))
            .willReturn(Optional.of(testPokemon));
        given(pokemonRepo.findByName(testFavouritePokemonName))
            .willReturn(Optional.of(testFavouritePokemon));

        pokemonServiceImpl.addPokemonToFavourites(TEST_POKEMON_ID, any());

        verify(pokemonRepo)
            .findByName(testFavouritePokemonName);
        assertThat(testFavouritePokemon.getNumberOfLikes())
            .isEqualTo(0);
        assertThat(loggedTestUser.getFavouritePokemonName())
            .isEqualTo(TEST_POKEMON_NAME);
        assertThat(testPokemon.getNumberOfLikes())
            .isEqualTo(1);
    }

    @Test
    void addPokemonToFavourites_userNotLoggedIn_throwResponseStatusException() {
        given(userService.getLoggedUser(any()))
            .willReturn(null);

        assertThatThrownBy(() -> pokemonServiceImpl.addPokemonToFavourites(TEST_POKEMON_ID, any()))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, UNAUTHORIZED)
            .hasMessageContaining(USER_NOT_LOGGED_MESS);
        verify(pokemonRepo, never())
            .findById(any());
        verify(pokemonRepo, never())
            .findByName(any());
    }

    @Test
    void getAllPokemons_validParameters_findByNameContainingMethodInvoked() {
        final var requestedPageable = getPageable(
            0, 10, "ASC", "id"
        );
        final Page<Pokemon> testPokemonPage = new PageImpl<>(emptyList());
        given(pokemonRepo.findByNameContaining("", requestedPageable))
            .willReturn(testPokemonPage);

        pokemonServiceImpl.getAllPokemons(0, 10, "ASC", "id", "");

        verify(pokemonRepo)
            .findByNameContaining("", requestedPageable);
    }

    @Test
    void getAllPokemons_invalidParameter_throwResponseStatusException() {
        assertThatThrownBy(() -> pokemonServiceImpl.getAllPokemons(
            0, 10, INVALID_SORT, "id", ""
        ))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, BAD_REQUEST)
            .hasMessageContaining(INVALID_REQUEST_PARAMS_MESS);

        verify(pokemonRepo, never())
            .findByNameContaining(any(), any());
    }

    @Test
    void getTopPokemon_pokemonOfHighestNumberOfLikes() {
        final var testPokemon1 = createTestPokemon(TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1);
        final var testPokemon2 = createTestPokemon(TEST_POKEMON_EVOLUTIONS_NONE, TEST_POKEMON_TYPES_1);
        testPokemon1.setNumberOfLikes(2);
        final var testPokemonPage = new PageImpl<>(List.of(
            testPokemon1, testPokemon2
        ));

        given(pokemonRepo.findByNameContaining(any(), any()))
            .willReturn(testPokemonPage);

        final var expectedPokemon = pokemonServiceImpl.getTopPokemon();

        assertThat(expectedPokemon.getNumberOfLikes())
            .isEqualTo(2);
    }

    @Test
    void getRandomPokemon_somePokemon() {
        given(pokemonRepo.findById(any()))
            .willReturn(Optional.of(testPokemon));

        final var expectedPokemon = pokemonServiceImpl.getRandomPokemon();

        assertThat(expectedPokemon)
            .isEqualTo(testPokemon);
    }

}
