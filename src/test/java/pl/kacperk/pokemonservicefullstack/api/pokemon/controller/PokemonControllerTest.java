package pl.kacperk.pokemonservicefullstack.api.pokemon.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.AbstractControllerTest;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.dto.mapper.PokemonResponseDtoMapper;
import pl.kacperk.pokemonservicefullstack.api.pokemon.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;
import pl.kacperk.pokemonservicefullstack.util.pagenavigation.PageLimitsCalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
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
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.ID_PROP;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.LOGIN_URL;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.NAME_PROP;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.PHOTO_URL_PROP;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.POSSIBLE_EVOLUTIONS_PROP;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTERED_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTERED_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTER_REQUEST_DTO;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_FIELD_TO_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_MATCH;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_NUM;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_SIZE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_ID;

class PokemonControllerTest extends AbstractControllerTest {

    private static final String POKEMONS_GET_POKEMON_MAPPING = "/api/pokemons/get/pokemon";
    private static final String POKEMONS_GET_RANDOM_MAPPING = "/api/pokemons/get/random";
    private static final String POKEMONS_GET_ALL_MAPPING = "/api/pokemons/get/all";
    private static final String POKEMONS_GET_TOP_MAPPING = "/api/pokemons/get/top";
    private static final String POKEMONS_LIKE_MAPPING = "/api/pokemons/like/{id}";

    private static final String ID_PARAM = "id";
    private static final String NAME_PARAM = "name";
    private static final String PAGE_NUM_PARAM = "pageNum";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String SORT_DIR_PARAM = "sortDir";
    private static final String SORT_BY_PARAM = "sortBy";
    private static final String MATCH_BY_PARAM = "matchBy";

    private static final String POSSIBLE_EVOLUTIONS_SET_ATR = "possibleEvolutionsSet";
    private static final String POSSIBLE_EVOLUTIONS_ATR = "possibleEvolutions";
    private static final String POKEMON_ATR = "pokemon";
    private static final String PAGE_NUM_ATR = "pageNum";
    private static final String PAGE_SIZE_ATR = "pageSize";
    private static final String SORT_DIR_ATR = "sortDir";
    private static final String SORT_BY_ATR = "sortBy";
    private static final String MATCH_BY_ATR = "matchBy";
    private static final String POKEMONS_ATR = "pokemons";
    private static final String ALL_PAGES_ATR = "allPages";
    private static final String TOTAL_ELEMENTS_ATR = "totalElements";
    private static final String PAGE_LEFT_LIMIT_ATR = "pageLeftLimit";
    private static final String PAGE_RIGHT_LIMIT_ATR = "pageRightLimit";
    private static final String TOP_POKEMONS_ATR = "topPokemons";

    private static final String NUMBER_OF_LIKES_PROP = "numberOfLikes";
    private static final String TYPE_NAMES_PROP = "typeNames";

    private static final String POKEMON_VIEW_NAME = "pokemon";
    private static final String DATABASE_VIEW_NAME = "database";
    private static final String RANKING_VIEW_NAME = "ranking";

    private static final String POKEMON_FAVOURITE_URL = "/pokemon-favourite";

    private static final String GET_POKEMON_NO_PARAMS_ERROR = "Pokemon can only be found by its id or name";
    private static final int MIN_EVOLUTIONS = 0;
    private static final int MAX_EVOLUTIONS = 2;
    private static final long MIN_POKEMON_ID = 1;
    private static final long MAX_POKEMON_ID = 905;
    private static final int NON_DEF_PAGE_NUM = 1;
    private static final int NON_DEF_PAGE_SIZE = 40;
    private static final String NON_DEF_FIELD_TO_SORT = "name";
    private static final String NON_DEF_MATCH = "B";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PokemonService pokemonService;
    @Autowired
    private AppUserService userService;
    @Autowired
    private AppUserRepo userRepo;

    @BeforeEach
    void setUp() throws UserAlreadyExistException {
        userService.registerAppUser(REGISTER_REQUEST_DTO);
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
    }

    @Test
    @Transactional
    void getPokemon_idParamAnonymousUser_correctModelAttributesStatusView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        final var expectedEvolutionsSet = testPokemon.getPossibleEvolutions();
        final var expectedEvolutions = expectedEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_POKEMON_MAPPING)
                .param(ID_PARAM, String.valueOf(TEST_POKEMON_ID))
        );

        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_SET_ATR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_ATR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(POSSIBLE_EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW_NAME)
        );
    }

    @Test
    @Transactional
    void getPokemon_idParamLoggedUser_correctModelAttributesStatusAndView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        final var expectedEvolutionsSet = testPokemon.getPossibleEvolutions();
        final var expectedEvolutions = expectedEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_POKEMON_MAPPING)
                .param(ID_PARAM, String.valueOf(TEST_POKEMON_ID))
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_SET_ATR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_ATR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(POSSIBLE_EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW_NAME)
        );
    }

    @Test
    @Transactional
    void getPokemon_nameParamAnonymousUser_correctModelAttributesStatusAndView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        final var testPokemonName = testPokemon.getName();
        final var expectedEvolutionsSet = testPokemon.getPossibleEvolutions();
        final var expectedEvolutions = expectedEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_POKEMON_MAPPING)
                .param(NAME_PARAM, testPokemonName)
        );

        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_SET_ATR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_ATR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(POSSIBLE_EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW_NAME)
        );
    }

    @Test
    @Transactional
    void getPokemon_nameParamLoggedUser_correctModelAttributesStatusAndView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        final var testPokemonName = testPokemon.getName();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );
        final var expectedEvolutionsSet = testPokemon.getPossibleEvolutions();
        final var expectedEvolutions = expectedEvolutionsSet.size();
        final var expectedPokemonResponse = PokemonResponseDtoMapper.pokemonToPokemonResponseDto(testPokemon);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_POKEMON_MAPPING)
                .param(NAME_PARAM, testPokemonName)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_SET_ATR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_ATR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(POSSIBLE_EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW_NAME)
        );
    }

    @Test
    void getPokemon_noParamsAnonymousUser_throwResponseStatusException() throws Exception {
        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_POKEMON_MAPPING)
        );

        resultActions.andExpect(
            status().isBadRequest()
        );
        resultActions.andExpect(
            result -> assertThat(result.getResolvedException())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(GET_POKEMON_NO_PARAMS_ERROR)
        );
    }

    @Test
    void getPokemon_noParamsLoggedUser_throwResponseStatusException() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_POKEMON_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isBadRequest()
        );
        resultActions.andExpect(
            result -> assertThat(result.getResolvedException())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(GET_POKEMON_NO_PARAMS_ERROR)
        );
    }

    @Test
    void getRandomPokemon_anonymousUser_correctModelAttributesStatusView() throws Exception {
        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_RANDOM_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_SET_ATR, hasSize(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_ATR, is(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATR, allOf(
                hasProperty(ID_PARAM, is(
                    both(greaterThanOrEqualTo(MIN_POKEMON_ID))
                        .and(lessThanOrEqualTo(MAX_POKEMON_ID))
                )),
                hasProperty(NAME_PROP, is(notNullValue())),
                hasProperty(POSSIBLE_EVOLUTIONS_PROP),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(notNullValue())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(0))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW_NAME)
        );
    }

    @Test
    void getRandomPokemon_loggedUser_correctModelAttributesStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_RANDOM_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_SET_ATR, hasSize(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(POSSIBLE_EVOLUTIONS_ATR, is(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATR, allOf(
                hasProperty(ID_PARAM, is(
                    both(greaterThanOrEqualTo(MIN_POKEMON_ID))
                        .and(lessThanOrEqualTo(MAX_POKEMON_ID))
                )),
                hasProperty(NAME_PROP, is(notNullValue())),
                hasProperty(POSSIBLE_EVOLUTIONS_PROP),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(notNullValue())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(0))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW_NAME)
        );
    }

    @Test
    void getAllPokemons_defaultParamsAnonymousUser_correctModelAttributesStatusView() throws Exception {
        final var expectedPokemons = pokemonService.getAll(
            DEF_PAGE_NUM, DEF_PAGE_SIZE, DEF_SORT, DEF_FIELD_TO_SORT, DEF_MATCH
        );
        final var expectedTotalPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedTotalPages, DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(PAGE_NUM_ATR, is(DEF_PAGE_NUM))
        );
        resultActions.andExpect(
            model().attribute(PAGE_SIZE_ATR, is(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(SORT_DIR_ATR, is(DEF_SORT))
        );
        resultActions.andExpect(
            model().attribute(SORT_BY_ATR, is(DEF_FIELD_TO_SORT))
        );
        resultActions.andExpect(
            model().attribute(MATCH_BY_ATR, is(DEF_MATCH))
        );
        resultActions.andExpect(
            model().attribute(POKEMONS_ATR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATR, is(expectedTotalPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW_NAME)
        );
    }

    @Test
    void getAllPokemons_defaultParamsLoggedUser_correctModelAttributesStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );
        final var expectedPokemons = pokemonService.getAll(
            DEF_PAGE_NUM, DEF_PAGE_SIZE, DEF_SORT, DEF_FIELD_TO_SORT, DEF_MATCH
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(PAGE_NUM_ATR, is(DEF_PAGE_NUM))
        );
        resultActions.andExpect(
            model().attribute(PAGE_SIZE_ATR, is(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(SORT_DIR_ATR, is(DEF_SORT))
        );
        resultActions.andExpect(
            model().attribute(SORT_BY_ATR, is(DEF_FIELD_TO_SORT))
        );
        resultActions.andExpect(
            model().attribute(MATCH_BY_ATR, is(DEF_MATCH))
        );
        resultActions.andExpect(
            model().attribute(POKEMONS_ATR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATR, is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW_NAME)
        );
    }

    @Test
    void getAllPokemons_nonDefaultParamsAnonymousUser_correctModelAttributesStatusView() throws Exception {
        final var expectedPokemons = pokemonService.getAll(
            NON_DEF_PAGE_NUM, NON_DEF_PAGE_SIZE, NON_DEF_SORT, NON_DEF_FIELD_TO_SORT, NON_DEF_MATCH
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, NON_DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
                .param(PAGE_NUM_PARAM, String.valueOf(NON_DEF_PAGE_NUM))
                .param(PAGE_SIZE_PARAM, String.valueOf(NON_DEF_PAGE_SIZE))
                .param(SORT_DIR_PARAM, NON_DEF_SORT)
                .param(SORT_BY_PARAM, NON_DEF_FIELD_TO_SORT)
                .param(MATCH_BY_PARAM, NON_DEF_MATCH)
        );

        resultActions.andExpect(
            model().attribute(PAGE_NUM_ATR, is(NON_DEF_PAGE_NUM))
        );
        resultActions.andExpect(
            model().attribute(PAGE_SIZE_ATR, is(NON_DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(SORT_DIR_ATR, is(NON_DEF_SORT))
        );
        resultActions.andExpect(
            model().attribute(SORT_BY_ATR, is(NON_DEF_FIELD_TO_SORT))
        );
        resultActions.andExpect(
            model().attribute(MATCH_BY_ATR, is(NON_DEF_MATCH))
        );
        resultActions.andExpect(
            model().attribute(POKEMONS_ATR, hasSize(
                both(greaterThanOrEqualTo(0))
                    .and(lessThanOrEqualTo(NON_DEF_PAGE_SIZE))
            ))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATR, is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW_NAME)
        );
    }

    @Test
    void getAllPokemons_nonDefaultParamsLoggedUser_correctModelAttributesStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );
        final var expectedPokemons = pokemonService.getAll(
            NON_DEF_PAGE_NUM, NON_DEF_PAGE_SIZE, NON_DEF_SORT, NON_DEF_FIELD_TO_SORT, NON_DEF_MATCH
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, NON_DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
                .param(PAGE_NUM_PARAM, String.valueOf(NON_DEF_PAGE_NUM))
                .param(PAGE_SIZE_PARAM, String.valueOf(NON_DEF_PAGE_SIZE))
                .param(SORT_DIR_PARAM, NON_DEF_SORT)
                .param(SORT_BY_PARAM, NON_DEF_FIELD_TO_SORT)
                .param(MATCH_BY_PARAM, NON_DEF_MATCH)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(
                PAGE_NUM_ATR, is(NON_DEF_PAGE_NUM)
            ));
        resultActions.andExpect(
            model().attribute(
                PAGE_SIZE_ATR, is(NON_DEF_PAGE_SIZE)
            ));
        resultActions.andExpect(
            model().attribute(
                SORT_DIR_ATR, is(NON_DEF_SORT)
            ));
        resultActions.andExpect(
            model().attribute(
                SORT_BY_ATR, is(NON_DEF_FIELD_TO_SORT)
            ));
        resultActions.andExpect(
            model().attribute(
                MATCH_BY_ATR, is(NON_DEF_MATCH)
            ));
        resultActions.andExpect(
            model().attribute(POKEMONS_ATR, hasSize(
                both(greaterThanOrEqualTo(0))
                    .and(lessThanOrEqualTo(NON_DEF_PAGE_SIZE))
            ))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATR, is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW_NAME)
        );
    }

    @Test
    @Transactional
    void getTopPokemons_anonymousUser_correctModelAttributeStatusView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        testPokemon.like();

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_TOP_MAPPING)
        );
        testPokemon.unlike();

        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATR, hasItem(
                hasProperty(NUMBER_OF_LIKES_PROP, is(1))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(RANKING_VIEW_NAME)
        );
    }

    @Test
    @Transactional
    void getTopPokemons_loggedUser_correctModelAttributeStatusView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        testPokemon.like();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_TOP_MAPPING)
                .session(sessionWithLoggedUser)
        );
        testPokemon.unlike();

        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATR, hasItem(
                hasProperty(NUMBER_OF_LIKES_PROP, is(1))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(RANKING_VIEW_NAME)
        );
    }

    @Test
    void addPokemonToFavourites_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        final var testPokemonId = testPokemon.getId();

        final var resultActions = mockMvc.perform(
            get(POKEMONS_LIKE_MAPPING, testPokemonId)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    @Transactional
    void addPokemonToFavourites_loggedUser_correctStatusRedirectedUrl() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        final var pokemonId = testPokemon.getId();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMONS_LIKE_MAPPING, pokemonId)
                .session(sessionWithLoggedUser)
        );
        testPokemon.unlike();

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(POKEMON_FAVOURITE_URL)
        );
    }

}
