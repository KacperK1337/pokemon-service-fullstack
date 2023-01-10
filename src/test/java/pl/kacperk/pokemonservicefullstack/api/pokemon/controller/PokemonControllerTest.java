package pl.kacperk.pokemonservicefullstack.api.pokemon.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper.PokemonResponseDtoMapper;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.response.PokemonResponseDto;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;
import pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepo appUserRepo;

    private Pokemon firstDbPokemon;
    private AppUser controllerTestUser;
    private final String requestMappingUrl = "/api/pokemons";
    private final String controllerTestUserPassword = "controllerTestUserPassword";
    private final Integer defaultPageNumber = 0;
    private final Integer defaultPageSize = 20;
    private final String defaultSortDirectionName = "ASC";
    private final String defaultFieldToSortBy = "id";
    private final String defaultNameToMatch = "";

    @BeforeEach
    void setUp() throws UserAlreadyExistException {
        firstDbPokemon = pokemonService.getPokemonById(1L);
        String controllerTestAppUserName = "controllerTestAppUserName";
        TestUtils.prepareAppUserRepoForControllerTest(
                appUserRepo, appUserService, controllerTestAppUserName, controllerTestUserPassword
        );
        controllerTestUser = appUserService.getAppUserByName(controllerTestAppUserName);
    }

    @Test
    @Transactional
    void getPokemon_idParamAndWithAnonymousUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        Long pokemonId = firstDbPokemon.getId();
        Set<String> expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        int expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        PokemonResponseDto expectedPokemonResponse =
                PokemonResponseDtoMapper.pokemonToPokemonResponseDto(firstDbPokemon);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/pokemon")
                .param("id", String.valueOf(pokemonId))
        );

        // then
        resultActions.andExpect(model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet));
        resultActions.andExpect(model().attribute("possibleEvolutions", expectedPossibleEvolutions));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("id", is(expectedPokemonResponse.getId()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("name", is(expectedPokemonResponse.getName()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("photoUrl", is(expectedPokemonResponse.getPhotoUrl()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("typeNames", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("possibleEvolutions")
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes()))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("pokemon"));
    }

    @Test
    @Transactional
    void getPokemon_idParamAndWithLoggedUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        Long pokemonId = firstDbPokemon.getId();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);
        Set<String> expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        int expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        PokemonResponseDto expectedPokemonResponse =
                PokemonResponseDtoMapper.pokemonToPokemonResponseDto(firstDbPokemon);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/pokemon")
                .param("id", String.valueOf(pokemonId))
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet));
        resultActions.andExpect(model().attribute("possibleEvolutions", expectedPossibleEvolutions));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("id", is(expectedPokemonResponse.getId()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("name", is(expectedPokemonResponse.getName()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("photoUrl", is(expectedPokemonResponse.getPhotoUrl()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("typeNames", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("possibleEvolutions", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes()))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("pokemon"));
    }

    @Test
    @Transactional
    void getPokemon_nameParamAndWithAnonymousUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        String pokemonName = firstDbPokemon.getName();
        Set<String> expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        int expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        PokemonResponseDto expectedPokemonResponse =
                PokemonResponseDtoMapper.pokemonToPokemonResponseDto(firstDbPokemon);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/pokemon")
                .param("name", String.valueOf(pokemonName))
        );

        // then
        resultActions.andExpect(model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet));
        resultActions.andExpect(model().attribute("possibleEvolutions", expectedPossibleEvolutions));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("id", is(expectedPokemonResponse.getId()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("name", is(expectedPokemonResponse.getName()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("photoUrl", is(expectedPokemonResponse.getPhotoUrl()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("typeNames", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("possibleEvolutions", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes()))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("pokemon"));
    }

    @Test
    @Transactional
    void getPokemon_nameParamAndWithLoggedUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        String pokemonName = firstDbPokemon.getName();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);
        Set<String> expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        int expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        PokemonResponseDto expectedPokemonResponse =
                PokemonResponseDtoMapper.pokemonToPokemonResponseDto(firstDbPokemon);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/pokemon")
                .param("name", String.valueOf(pokemonName))
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet));
        resultActions.andExpect(model().attribute("possibleEvolutions", expectedPossibleEvolutions));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("id", is(expectedPokemonResponse.getId()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("name", is(expectedPokemonResponse.getName()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("photoUrl", is(expectedPokemonResponse.getPhotoUrl()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("typeNames", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("possibleEvolutions", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes()))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("pokemon"));
    }

    @Test
    void getPokemon_noParamsWithAnonymousUser_throwResponseStatusException() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/pokemon"));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(result ->
                assertThat(result.getResolvedException()).isInstanceOf(ResponseStatusException.class)
        );
        resultActions.andExpect(result ->
                assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .contains("Pokemon can only be found by its id or name")
        );
    }

    @Test
    void getPokemon_noParamsWithLoggedUser_throwResponseStatusException() throws Exception {
        // given
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/pokemon")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(result ->
                assertThat(result.getResolvedException()).isInstanceOf(ResponseStatusException.class)
        );
        resultActions.andExpect(result ->
                assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                        .contains("Pokemon can only be found by its id or name")
        );
    }

    @Test
    void getRandomPokemon_WithAnonymousUser_correctModelAttributesAndStatusAndView() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/random"));

        // then
        resultActions.andExpect(model().attribute("possibleEvolutionsSet",
                hasSize(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2)))
        ));
        resultActions.andExpect(model().attribute("possibleEvolutions",
                is(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2)))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("id", is(both(greaterThanOrEqualTo(1L)).and(lessThanOrEqualTo(905L))))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("name", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("photoUrl", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("typeNames", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("possibleEvolutions")
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("numberOfLikes", is(0))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("pokemon"));
    }

    @Test
    void getRandomPokemon_WithLoggedUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/random")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("possibleEvolutionsSet",
                hasSize(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2)))
        ));
        resultActions.andExpect(model().attribute("possibleEvolutions",
                is(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2)))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("id", is(both(greaterThanOrEqualTo(1L)).and(lessThanOrEqualTo(905L))))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("name", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("photoUrl", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("typeNames", is(notNullValue()))
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("possibleEvolutions")
        ));
        resultActions.andExpect(model().attribute("pokemon",
                hasProperty("numberOfLikes", is(0))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("pokemon"));
    }

    @Test
    void getAllPokemons_defaultParamsWithAnonymousUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        Page<Pokemon> expectedPokemons =
                pokemonService.getAll(defaultPageNumber, defaultPageSize, defaultSortDirectionName,
                        defaultFieldToSortBy, defaultNameToMatch);
        int expectedAllPages = expectedPokemons.getTotalPages();
        long expectedTotalElements = expectedPokemons.getTotalElements();
        int[] expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, defaultPageNumber);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/all"));

        // then
        resultActions.andExpect(model().attribute("pageNum", is(defaultPageNumber)));
        resultActions.andExpect(model().attribute("pageSize", is(defaultPageSize)));
        resultActions.andExpect(model().attribute("sortDir", is(defaultSortDirectionName)));
        resultActions.andExpect(model().attribute("sortBy", is(defaultFieldToSortBy)));
        resultActions.andExpect(model().attribute("matchBy", is(defaultNameToMatch)));

        resultActions.andExpect(model().attribute("pokemons",
                hasSize(defaultPageSize)
        ));
        resultActions.andExpect(model().attribute("allPages", is(expectedAllPages)));
        resultActions.andExpect(model().attribute("totalElements", is(expectedTotalElements)));
        resultActions.andExpect(model().attribute("pageLeftLimit", is(expectedPageLimits[0])));
        resultActions.andExpect(model().attribute("pageRightLimit", is(expectedPageLimits[1])));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("database"));
    }

    @Test
    void getAllPokemons_defaultParamsWithLoggedUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);
        Page<Pokemon> expectedPokemons =
                pokemonService.getAll(defaultPageNumber, defaultPageSize, defaultSortDirectionName,
                        defaultFieldToSortBy, defaultNameToMatch);
        int expectedAllPages = expectedPokemons.getTotalPages();
        long expectedTotalElements = expectedPokemons.getTotalElements();
        int[] expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, defaultPageNumber);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/all")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("pageNum", is(defaultPageNumber)));
        resultActions.andExpect(model().attribute("pageSize", is(defaultPageSize)));
        resultActions.andExpect(model().attribute("sortDir", is(defaultSortDirectionName)));
        resultActions.andExpect(model().attribute("sortBy", is(defaultFieldToSortBy)));
        resultActions.andExpect(model().attribute("matchBy", is(defaultNameToMatch)));

        resultActions.andExpect(model().attribute("pokemons",
                hasSize(defaultPageSize)
        ));
        resultActions.andExpect(model().attribute("allPages", is(expectedAllPages)));
        resultActions.andExpect(model().attribute("totalElements", is(expectedTotalElements)));
        resultActions.andExpect(model().attribute("pageLeftLimit", is(expectedPageLimits[0])));
        resultActions.andExpect(model().attribute("pageRightLimit", is(expectedPageLimits[1])));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("database"));
    }

    @Test
    void getAllPokemons_nonDefaultParamsWithAnonymousUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        Integer pageNumber = 1;
        Integer pageSize = 40;
        String sortDirectionName = "DESC";
        String fieldToSortBy = "name";
        String nameToMatch = "B";

        Page<Pokemon> expectedPokemons =
                pokemonService.getAll(pageNumber, pageSize, sortDirectionName,
                        fieldToSortBy, nameToMatch);
        int expectedAllPages = expectedPokemons.getTotalPages();
        long expectedTotalElements = expectedPokemons.getTotalElements();
        int[] expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, pageNumber);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/all")
                .param("pageNum", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize))
                .param("sortDir", sortDirectionName)
                .param("sortBy", fieldToSortBy)
                .param("matchBy", nameToMatch)
        );

        // then
        resultActions.andExpect(model().attribute("pageNum", is(pageNumber)));
        resultActions.andExpect(model().attribute("pageSize", is(pageSize)));
        resultActions.andExpect(model().attribute("sortDir", is(sortDirectionName)));
        resultActions.andExpect(model().attribute("sortBy", is(fieldToSortBy)));
        resultActions.andExpect(model().attribute("matchBy", is(nameToMatch)));

        resultActions.andExpect(model().attribute("pokemons",
                hasSize(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(pageSize)))
        ));
        resultActions.andExpect(model().attribute("allPages", is(expectedAllPages)));
        resultActions.andExpect(model().attribute("totalElements", is(expectedTotalElements)));
        resultActions.andExpect(model().attribute("pageLeftLimit", is(expectedPageLimits[0])));
        resultActions.andExpect(model().attribute("pageRightLimit", is(expectedPageLimits[1])));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("database"));
    }

    @Test
    void getAllPokemons_nonDefaultParamsWithLoggedUser_correctModelAttributesAndStatusAndView() throws Exception {
        // given
        Integer pageNumber = 1;
        Integer pageSize = 40;
        String sortDirectionName = "DESC";
        String fieldToSortBy = "name";
        String nameToMatch = "B";

        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);
        Page<Pokemon> expectedPokemons =
                pokemonService.getAll(pageNumber, pageSize, sortDirectionName,
                        fieldToSortBy, nameToMatch);
        int expectedAllPages = expectedPokemons.getTotalPages();
        long expectedTotalElements = expectedPokemons.getTotalElements();
        int[] expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, pageNumber);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/all")
                .param("pageNum", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize))
                .param("sortDir", sortDirectionName)
                .param("sortBy", fieldToSortBy)
                .param("matchBy", nameToMatch)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("pageNum", is(pageNumber)));
        resultActions.andExpect(model().attribute("pageSize", is(pageSize)));
        resultActions.andExpect(model().attribute("sortDir", is(sortDirectionName)));
        resultActions.andExpect(model().attribute("sortBy", is(fieldToSortBy)));
        resultActions.andExpect(model().attribute("matchBy", is(nameToMatch)));

        resultActions.andExpect(model().attribute("pokemons",
                hasSize(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(pageSize)))
        ));
        resultActions.andExpect(model().attribute("allPages", is(expectedAllPages)));
        resultActions.andExpect(model().attribute("totalElements", is(expectedTotalElements)));
        resultActions.andExpect(model().attribute("pageLeftLimit", is(expectedPageLimits[0])));
        resultActions.andExpect(model().attribute("pageRightLimit", is(expectedPageLimits[1])));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("database"));
    }

    @Test
    void getTopPokemons_withAnonymousUser_correctModelAttributeAndStatusAndView() throws Exception {
        // given
        firstDbPokemon.like();

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/top"));

        // then
        resultActions.andExpect(model().attribute("topPokemons", hasSize(20)));
        resultActions.andExpect(model().attribute("topPokemons",
                hasItem(hasProperty("numberOfLikes", is(1)))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("ranking"));

        firstDbPokemon.unlike();
    }

    @Test
    void getTopPokemons_withLoggedUser_correctModelAttributeAndStatusAndView() throws Exception {
        // given
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);
        firstDbPokemon.like();

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/get/top")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("topPokemons", hasSize(20)));
        resultActions.andExpect(model().attribute("topPokemons",
                hasItem(hasProperty("numberOfLikes", is(1)))
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("ranking"));

        firstDbPokemon.unlike();
    }

    @Test
    void addPokemonToFavourites_withAnonymousUser_correctStatusAndRedirectedUrl() throws Exception {
        // given
        Long pokemonId = firstDbPokemon.getId();

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/like/{id}", pokemonId));

        // then
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    void addPokemonToFavourites_withLoggedUser_correctStatusAndRedirectedUrl() throws Exception {
        // given
        Long pokemonId = firstDbPokemon.getId();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/like/{id}", pokemonId)
                .session(sessionWithLoggedUser));

        // then
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(redirectedUrl("/pokemon-favourite"));

        firstDbPokemon.unlike();
    }
}
