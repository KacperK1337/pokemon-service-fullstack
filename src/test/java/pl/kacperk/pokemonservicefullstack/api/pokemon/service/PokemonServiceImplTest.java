package pl.kacperk.pokemonservicefullstack.api.pokemon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Type;
import pl.kacperk.pokemonservicefullstack.api.pokemon.repo.PokemonRepo;
import pl.kacperk.pokemonservicefullstack.util.pageable.PageableCreator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    @Mock
    private PokemonRepo pokemonRepo;

    @Mock
    private AppUserService appUserService;

    private PokemonServiceImpl underTest;
    private Pokemon testPokemon;
    private AppUser loggedTestUser;

    private Pokemon createTestPokemon(String name) {
        Set<String> possibleEvolutions = new LinkedHashSet<>();
        possibleEvolutions.add("testEvolution");
        Set<Type> types = new LinkedHashSet<>();
        types.add(Type.Water);
        return new Pokemon(name, possibleEvolutions, types, "testPhotoUrl");
    }

    private AppUser createTestAppUser() {
        return new AppUser(AppUserRole.USER, "testUserName", "testPassword");
    }

    @BeforeEach
    void setUp() {
        underTest = new PokemonServiceImpl(pokemonRepo, appUserService);
        testPokemon = createTestPokemon("testPokemon");
    }

    @Test
    void getPokemonById_existingId_findByIdMethodInvoked() {
        // given
        Long id = 1L;

        given(pokemonRepo.findById(id)).willReturn(Optional.of(testPokemon));

        // when
        underTest.getPokemonById(id);

        // then
        verify(pokemonRepo).findById(id);
    }

    @Test
    void getPokemonById_nonExistingId_throwResponseStatusException() {
        // given
        Long id = 10L;

        given(pokemonRepo.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getPokemonById(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining(String.format("Pokemon with id %s not found", id));
    }

    @Test
    void getPokemonByName_existingName_findByNameMethodInvoked() {
        // given
        String name = testPokemon.getName();

        given(pokemonRepo.findByName(name)).willReturn(Optional.of(testPokemon));

        // when
        underTest.getPokemonByName(name);

        // then
        verify(pokemonRepo).findByName(name);
    }

    @Test
    void getPokemonByName_nonExistingName_throwResponseStatusException() {
        // given
        String name = testPokemon.getName();
        String nonExistingName = name.toUpperCase();

        given(pokemonRepo.findByName(nonExistingName)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getPokemonByName(nonExistingName))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining(String.format("Pokemon with name %s not found", nonExistingName));
    }

    @Test
    void addPokemonToFavourites_loggedUserWithoutFavouritePokemon_loggedUserWithFavouritePokemonThatHas1Like() {
        // given
        Long id = 1L;
        loggedTestUser = createTestAppUser();

        given(appUserService.getLoggedAppUser(any())).willReturn(loggedTestUser);
        given(pokemonRepo.findById(id)).willReturn(Optional.of(testPokemon));

        // when
        underTest.addPokemonToFavourites(id, any());

        // then
        verify(pokemonRepo, never()).findByName(any());

        assertThat(loggedTestUser.getFavouritePokemonName()).isEqualTo(testPokemon.getName());
        assertThat(testPokemon.getNumberOfLikes()).isEqualTo(1);
    }

    @Test
    void addPokemonToFavourites_loggedUserWithFavouritePokemon_LoggedUserWithNewFavouritePokemonThatHas1Like() {
        // given
        Long id = 1L;
        loggedTestUser = createTestAppUser();
        String favouritePokemonName = "someTestName";
        Pokemon testFavouritePokemon = createTestPokemon(favouritePokemonName);
        testFavouritePokemon.setNumberOfLikes(1);
        loggedTestUser.setFavouritePokemonName(favouritePokemonName);

        given(appUserService.getLoggedAppUser(any())).willReturn(loggedTestUser);
        given(pokemonRepo.findById(id)).willReturn(Optional.of(testPokemon));
        given(pokemonRepo.findByName(favouritePokemonName)).willReturn(Optional.of(testFavouritePokemon));

        // when
        underTest.addPokemonToFavourites(id, any());

        // then
        verify(pokemonRepo).findByName(favouritePokemonName);

        assertThat(testFavouritePokemon.getNumberOfLikes()).isEqualTo(0);
        assertThat(loggedTestUser.getFavouritePokemonName()).isEqualTo(testPokemon.getName());
        assertThat(testPokemon.getNumberOfLikes()).isEqualTo(1);
    }

    @Test
    void addPokemonToFavourites_userNotLoggedIn_throwResponseStatusException() {
        // given
        Long id = 1L;

        given(appUserService.getLoggedAppUser(any())).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> underTest.addPokemonToFavourites(id, any()))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED)
                .hasMessageContaining("User is not logged in");

        verify(pokemonRepo, never()).findById(any());
        verify(pokemonRepo, never()).findByName(any());
    }

    @Test
    void getAll_validParameters_findByNameContainingMethodInvoked() {
        // given
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortDirectionName = "ASC";
        String fieldToSortBy = "id";
        String nameToMach = "";
        Pageable requestedPageable =
                PageableCreator.getPageable(pageNumber, pageSize, sortDirectionName, fieldToSortBy);

        List<Pokemon> testPokemonList = new ArrayList<>();
        Page<Pokemon> testPokemonPage = new PageImpl<>(testPokemonList);

        given(pokemonRepo.findByNameContaining(nameToMach, requestedPageable)).willReturn(testPokemonPage);

        // when
        underTest.getAll(pageNumber, pageSize, sortDirectionName, fieldToSortBy, nameToMach);

        // then
        verify(pokemonRepo).findByNameContaining(nameToMach, requestedPageable);
    }

    @Test
    void getAll_invalidParameter_throwResponseStatusException() {
        // given
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortDirectionName = "invalidParameter";
        String fieldToSortBy = "id";
        String nameToMach = "";

        // when
        // then
        assertThatThrownBy(() -> underTest.getAll(pageNumber, pageSize, sortDirectionName, fieldToSortBy, nameToMach))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("Invalid request parameters");

        verify(pokemonRepo, never()).findByNameContaining(any(), any());
    }

    @Test
    void getTopPokemon_pokemonHavingHighestNumberOfLikes() {
        // given
        Pokemon testPokemon1 = createTestPokemon("testPokemon1");
        Pokemon testPokemon2 = createTestPokemon("testPokemon2");
        testPokemon2.setNumberOfLikes(2);
        List<Pokemon> testPokemonList = new ArrayList<>();
        testPokemonList.add(testPokemon2);
        testPokemonList.add(testPokemon1);
        Page<Pokemon> testPokemonPage = new PageImpl<>(testPokemonList);

        given(pokemonRepo.findByNameContaining(any(), any())).willReturn(testPokemonPage);

        // when
        Pokemon expectedPokemon = underTest.getTopPokemon();

        // then
        assertThat(expectedPokemon.getNumberOfLikes()).isEqualTo(testPokemon2.getNumberOfLikes());
    }

    @Test
    void getRandomPokemon_somePokemon() {
        // given
        given(pokemonRepo.findById(any())).willReturn(Optional.of(testPokemon));

        // when
        Pokemon expectedPokemon = underTest.getRandomPokemon();

        // then
        assertThat(expectedPokemon).isEqualTo(testPokemon);
    }
}