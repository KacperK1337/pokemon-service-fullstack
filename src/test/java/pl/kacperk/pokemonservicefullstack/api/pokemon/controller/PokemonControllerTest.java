package pl.kacperk.pokemonservicefullstack.api.pokemon.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.ContainerTest;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper.PokemonResponseDtoMapper;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;
import pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator;

import static java.util.Objects.requireNonNull;
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
import static pl.kacperk.pokemonservicefullstack.TestUtils.getLoggedUserSession;

@SpringBootTest
@AutoConfigureMockMvc
public class PokemonControllerTest extends ContainerTest {

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

    private static final String CONTROLLER_MAPPING = "/api/pokemons";
    private static final String CONTROLLER_TEST_USER_PASS = "controllerTestUserPass";
    private static final Integer defaultPageNumber = 0;
    private static final Integer defaultPageSize = 20;
    private static final String defaultSortDirectionName = "ASC";
    private static final String defaultFieldToSortBy = "id";
    private static final String defaultNameToMatch = "";

    @BeforeEach
    void setUp() throws UserAlreadyExistException {
        firstDbPokemon = pokemonService.getPokemonById(1L);
        final var controllerTestAppUserName = "controllerTestAppUserName";
        TestUtils.prepareAppUserRepoForControllerTest(
            appUserRepo, appUserService, controllerTestAppUserName, CONTROLLER_TEST_USER_PASS
        );
        controllerTestUser = appUserService.getAppUserByName(controllerTestAppUserName);
    }

    @Test
    @Transactional
    void getPokemon_idParamAnonymousUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var pokemonId = firstDbPokemon.getId();
        final var expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        final var expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(
            firstDbPokemon
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/pokemon")
                .param("id", String.valueOf(pokemonId))
        );

        // then
        resultActions.andExpect(
            model().attribute(
                "possibleEvolutionsSet", expectedPossibleEvolutionsSet
            )
        );
        resultActions.andExpect(
            model().attribute(
                "possibleEvolutions", expectedPossibleEvolutions
            )
        );
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "id", is(expectedPokemonResponse.getId())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "name", is(expectedPokemonResponse.getName())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "photoUrl", is(expectedPokemonResponse.getPhotoUrl())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "typeNames", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty("possibleEvolutions")
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes())
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("pokemon")
        );
    }

    @Test
    @Transactional
    void getPokemon_idParamLoggedUser_correctModelAttributesStatusAndView() throws Exception {
        // given
        final var pokemonId = firstDbPokemon.getId();
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );
        final var expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        final var expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(
            firstDbPokemon
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/pokemon")
                .param("id", String.valueOf(pokemonId))
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute("possibleEvolutions", expectedPossibleEvolutions)
        );
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "id", is(expectedPokemonResponse.getId())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "name", is(expectedPokemonResponse.getName())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "photoUrl", is(expectedPokemonResponse.getPhotoUrl())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "typeNames", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "possibleEvolutions", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes())
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("pokemon")
        );
    }

    @Test
    @Transactional
    void getPokemon_nameParamAnonymousUser_correctModelAttributesStatusAndView() throws Exception {
        // given
        final var pokemonName = firstDbPokemon.getName();
        final var expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        final var expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(
            firstDbPokemon
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/pokemon")
                .param("name", String.valueOf(pokemonName))
        );

        // then
        resultActions.andExpect(
            model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute("possibleEvolutions", expectedPossibleEvolutions)
        );
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "id", is(expectedPokemonResponse.getId())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "name", is(expectedPokemonResponse.getName())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "photoUrl", is(expectedPokemonResponse.getPhotoUrl())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "typeNames", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "possibleEvolutions", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes())
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("pokemon")
        );
    }

    @Test
    @Transactional
    void getPokemon_nameParamLoggedUser_correctModelAttributesStatusAndView() throws Exception {
        // given
        final var pokemonName = firstDbPokemon.getName();
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );
        final var expectedPossibleEvolutionsSet = firstDbPokemon.getPossibleEvolutions();
        final var expectedPossibleEvolutions = expectedPossibleEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(
            firstDbPokemon
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/pokemon")
                .param("name", String.valueOf(pokemonName))
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute("possibleEvolutionsSet", expectedPossibleEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute("possibleEvolutions", expectedPossibleEvolutions)
        );
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "id", is(expectedPokemonResponse.getId())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "name", is(expectedPokemonResponse.getName())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "photoUrl", is(expectedPokemonResponse.getPhotoUrl())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty("typeNames", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "possibleEvolutions", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "numberOfLikes", is(expectedPokemonResponse.getNumberOfLikes())
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("pokemon")
        );
    }

    @Test
    void getPokemon_noParamsAnonymousUser_throwResponseStatusException() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/pokemon")
        );

        // then
        resultActions.andExpect(
            status().isBadRequest()
        );
        resultActions.andExpect(
            result -> assertThat(result.getResolvedException())
                .isInstanceOf(ResponseStatusException.class)
        );
        resultActions.andExpect(
            result -> assertThat(
                requireNonNull(result.getResolvedException()).getMessage()
            )
                .contains("Pokemon can only be found by its id or name")
        );
    }

    @Test
    void getPokemon_noParamsLoggedUser_throwResponseStatusException() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/pokemon")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            status().isBadRequest()
        );
        resultActions.andExpect(
            result -> assertThat(result.getResolvedException())
                .isInstanceOf(ResponseStatusException.class)
        );
        resultActions.andExpect(
            result -> assertThat(
                requireNonNull(result.getResolvedException()).getMessage()
            )
                .contains("Pokemon can only be found by its id or name")
        );
    }

    @Test
    void getRandomPokemon_anonymousUser_correctModelAttributesStatusView() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/random")
        );

        // then
        resultActions.andExpect(
            model().attribute(
                "possibleEvolutionsSet", hasSize(
                    both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2))
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "possibleEvolutions", is(
                    both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2))
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "id", is(both(greaterThanOrEqualTo(1L)).and(lessThanOrEqualTo(905L)))
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "name", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "photoUrl", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "typeNames", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty("possibleEvolutions")
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "numberOfLikes", is(0)
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("pokemon")
        );
    }

    @Test
    void getRandomPokemon_loggedUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/random")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute(
                "possibleEvolutionsSet", hasSize(
                    both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2))
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "possibleEvolutions", is(
                    both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(2))
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "id", is(both(greaterThanOrEqualTo(1L)).and(lessThanOrEqualTo(905L)))
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "name", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "photoUrl", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "typeNames", is(notNullValue())
                )
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty("possibleEvolutions")
            ));
        resultActions.andExpect(
            model().attribute(
                "pokemon", hasProperty(
                    "numberOfLikes", is(0)
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("pokemon")
        );
    }

    @Test
    void getAllPokemons_defaultParamsAnonymousUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var expectedPokemons = pokemonService.getAll(
            defaultPageNumber, defaultPageSize, defaultSortDirectionName, defaultFieldToSortBy, defaultNameToMatch
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, defaultPageNumber);

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/all")
        );

        // then
        resultActions.andExpect(
            model().attribute("pageNum", is(defaultPageNumber))
        );
        resultActions.andExpect(
            model().attribute("pageSize", is(defaultPageSize))
        );
        resultActions.andExpect(
            model().attribute("sortDir", is(defaultSortDirectionName))
        );
        resultActions.andExpect(
            model().attribute("sortBy", is(defaultFieldToSortBy))
        );
        resultActions.andExpect(
            model().attribute("matchBy", is(defaultNameToMatch))
        );

        resultActions.andExpect(
            model().attribute("pokemons", hasSize(defaultPageSize)
            ));
        resultActions.andExpect(
            model().attribute("allPages", is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute("totalElements", is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute("pageLeftLimit", is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute("pageRightLimit", is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("database")
        );
    }

    @Test
    void getAllPokemons_defaultParamsLoggedUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );
        final var expectedPokemons = pokemonService.getAll(
            defaultPageNumber, defaultPageSize, defaultSortDirectionName, defaultFieldToSortBy, defaultNameToMatch
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, defaultPageNumber);

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/all")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute("pageNum", is(defaultPageNumber))
        );
        resultActions.andExpect(
            model().attribute("pageSize", is(defaultPageSize))
        );
        resultActions.andExpect(
            model().attribute("sortDir", is(defaultSortDirectionName))
        );
        resultActions.andExpect(
            model().attribute("sortBy", is(defaultFieldToSortBy))
        );
        resultActions.andExpect(
            model().attribute("matchBy", is(defaultNameToMatch))
        );

        resultActions.andExpect(
            model().attribute("pokemons", hasSize(defaultPageSize)
            ));
        resultActions.andExpect(
            model().attribute("allPages", is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute("totalElements", is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute("pageLeftLimit", is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute("pageRightLimit", is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("database")
        );
    }

    @Test
    void getAllPokemons_nonDefaultParamsAnonymousUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var pageNumber = 1;
        final var pageSize = 40;
        final var sortDirectionName = "DESC";
        final var fieldToSortBy = "name";
        final var nameToMatch = "B";

        final var expectedPokemons = pokemonService.getAll(
            pageNumber, pageSize, sortDirectionName, fieldToSortBy, nameToMatch);
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, pageNumber);

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/all")
                .param("pageNum", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize))
                .param("sortDir", sortDirectionName)
                .param("sortBy", fieldToSortBy)
                .param("matchBy", nameToMatch)
        );

        // then
        resultActions.andExpect(
            model().attribute("pageNum", is(pageNumber))
        );
        resultActions.andExpect(
            model().attribute("pageSize", is(pageSize))
        );
        resultActions.andExpect(
            model().attribute("sortDir", is(sortDirectionName))
        );
        resultActions.andExpect(
            model().attribute("sortBy", is(fieldToSortBy))
        );
        resultActions.andExpect(
            model().attribute("matchBy", is(nameToMatch))
        );

        resultActions.andExpect(
            model().attribute("pokemons", hasSize(
                    both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(pageSize))
                )
            ));
        resultActions.andExpect(
            model().attribute("allPages", is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute("totalElements", is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute("pageLeftLimit", is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute("pageRightLimit", is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("database")
        );
    }

    @Test
    void getAllPokemons_nonDefaultParamsLoggedUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var pageNumber = 1;
        final var pageSize = 40;
        final var sortDirectionName = "DESC";
        final var fieldToSortBy = "name";
        final var nameToMatch = "B";

        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );
        final var expectedPokemons = pokemonService.getAll(
            pageNumber, pageSize, sortDirectionName, fieldToSortBy, nameToMatch
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, pageNumber);

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/all")
                .param("pageNum", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize))
                .param("sortDir", sortDirectionName)
                .param("sortBy", fieldToSortBy)
                .param("matchBy", nameToMatch)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute(
                "pageNum", is(pageNumber)
            ));
        resultActions.andExpect(
            model().attribute(
                "pageSize", is(pageSize)
            ));
        resultActions.andExpect(
            model().attribute(
                "sortDir", is(sortDirectionName)
            ));
        resultActions.andExpect(
            model().attribute(
                "sortBy", is(fieldToSortBy)
            ));
        resultActions.andExpect(
            model().attribute(
                "matchBy", is(nameToMatch)
            ));

        resultActions.andExpect(
            model().attribute("pokemons", hasSize(
                    both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(pageSize))
                )
            ));
        resultActions.andExpect(
            model().attribute("allPages", is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute("totalElements", is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute("pageLeftLimit", is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute("pageRightLimit", is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("database")
        );
    }

    @Test
    @Transactional
    void getTopPokemons_anonymousUser_correctModelAttributeStatusView() throws Exception {
        // given
        firstDbPokemon.like();

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/top")
        );

        // then
        resultActions.andExpect(
            model().attribute(
                "topPokemons", hasSize(20)
            ));
        resultActions.andExpect(
            model().attribute(
                "topPokemons", hasItem(
                    hasProperty("numberOfLikes", is(1))
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("ranking")
        );

        firstDbPokemon.unlike();
    }

    @Test
    @Transactional
    void getTopPokemons_loggedUser_correctModelAttributeStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );
        firstDbPokemon.like();

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/get/top")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute(
                "topPokemons", hasSize(20)
            ));
        resultActions.andExpect(
            model().attribute(
                "topPokemons", hasItem(
                    hasProperty("numberOfLikes", is(1))
                )
            ));
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("ranking")
        );

        firstDbPokemon.unlike();
    }

    @Test
    void addPokemonToFavourites_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        // given
        final var pokemonId = firstDbPokemon.getId();

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/like/{id}", pokemonId)
        );

        // then
        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl("http://localhost/auth/login")
        );
    }

    @Test
    void addPokemonToFavourites_loggedUser_correctStatusRedirectedUrl() throws Exception {
        // given
        final var pokemonId = firstDbPokemon.getId();
        final var sessionWithLoggedUser = getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            get(CONTROLLER_MAPPING + "/like/{id}", pokemonId)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl("/pokemon-favourite")
        );

        firstDbPokemon.unlike();
    }

}
