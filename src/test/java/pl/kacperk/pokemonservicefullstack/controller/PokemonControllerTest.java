package pl.kacperk.pokemonservicefullstack.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.AbstractControllerTest;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.entity.pokemon.dto.response.PokemonResponseDtoMapper;
import pl.kacperk.pokemonservicefullstack.service.PokemonService;
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
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.EVOLUTIONS_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.ID_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.LOGIN_URL;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.NAME_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.PHOTO_URL_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTER_REQUEST_DTO;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_FIELD_TO_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_MATCH;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_NUM;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_SIZE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_FIELD_TO_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_MATCH;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_PAGE_NUM;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_PAGE_SIZE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PokemonUtils.TEST_POKEMON_ID;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.ALL_PAGES_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.DATABASE_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.EVOLUTIONS_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.EVOLUTIONS_SET_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.GET_POKEMON_NO_PARAMS_ERROR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.ID_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.MATCH_BY_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.MATCH_BY_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.NAME_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.PAGE_LEFT_LIMIT_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.PAGE_NUM_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.PAGE_NUM_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.PAGE_RIGHT_LIMIT_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.PAGE_SIZE_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.PAGE_SIZE_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMONS_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMONS_GET_ALL_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMONS_GET_POKEMON_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMONS_GET_RANDOM_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMONS_GET_TOP_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMONS_LIKE_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMON_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMON_FAVOURITE_URL;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.POKEMON_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.RANKING_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.SORT_BY_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.SORT_BY_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.SORT_DIR_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.SORT_DIR_REQUEST_PARAM;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.TOP_POKEMONS_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.PokemonController.TOTAL_ELEMENTS_ATTR;

class PokemonControllerTest extends AbstractControllerTest {

    private static final String NUMBER_OF_LIKES_PROP = "numberOfLikes";
    private static final String TYPE_NAMES_PROP = "typeNames";

    private static final int MIN_EVOLUTIONS = 0;
    private static final int MAX_EVOLUTIONS = 2;
    private static final long MIN_POKEMON_ID = 1;
    private static final long MAX_POKEMON_ID = 905;

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
        userRepo.deleteAll();
        userService.registerUser(REGISTER_REQUEST_DTO);
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
                .param(ID_REQUEST_PARAM, String.valueOf(TEST_POKEMON_ID))
        );

        resultActions.andExpect(
            model().attribute(EVOLUTIONS_SET_ATTR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(EVOLUTIONS_ATTR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATTR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW)
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
                .param(ID_REQUEST_PARAM, String.valueOf(TEST_POKEMON_ID))
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(EVOLUTIONS_SET_ATTR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(EVOLUTIONS_ATTR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATTR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW)
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
                .param(NAME_REQUEST_PARAM, testPokemonName)
        );

        resultActions.andExpect(
            model().attribute(EVOLUTIONS_SET_ATTR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(EVOLUTIONS_ATTR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATTR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW)
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
                .param(NAME_REQUEST_PARAM, testPokemonName)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(EVOLUTIONS_SET_ATTR, expectedEvolutionsSet)
        );
        resultActions.andExpect(
            model().attribute(EVOLUTIONS_ATTR, expectedEvolutions)
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATTR, allOf(
                hasProperty(ID_PROP, is(expectedPokemonResponse.getId())),
                hasProperty(NAME_PROP, is(expectedPokemonResponse.getName())),
                hasProperty(EVOLUTIONS_PROP, is(notNullValue())),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(expectedPokemonResponse.getPhotoUrl())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(expectedPokemonResponse.getNumberOfLikes()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW)
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
            model().attribute(EVOLUTIONS_SET_ATTR, hasSize(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(EVOLUTIONS_ATTR, is(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATTR, allOf(
                hasProperty(ID_REQUEST_PARAM, is(
                    both(greaterThanOrEqualTo(MIN_POKEMON_ID))
                        .and(lessThanOrEqualTo(MAX_POKEMON_ID))
                )),
                hasProperty(NAME_PROP, is(notNullValue())),
                hasProperty(EVOLUTIONS_PROP),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(notNullValue())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(0))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW)
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
            model().attribute(EVOLUTIONS_SET_ATTR, hasSize(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(EVOLUTIONS_ATTR, is(
                both(greaterThanOrEqualTo(MIN_EVOLUTIONS))
                    .and(lessThanOrEqualTo(MAX_EVOLUTIONS))
            ))
        );
        resultActions.andExpect(
            model().attribute(POKEMON_ATTR, allOf(
                hasProperty(ID_REQUEST_PARAM, is(
                    both(greaterThanOrEqualTo(MIN_POKEMON_ID))
                        .and(lessThanOrEqualTo(MAX_POKEMON_ID))
                )),
                hasProperty(NAME_PROP, is(notNullValue())),
                hasProperty(EVOLUTIONS_PROP),
                hasProperty(TYPE_NAMES_PROP, is(notNullValue())),
                hasProperty(PHOTO_URL_PROP, is(notNullValue())),
                hasProperty(NUMBER_OF_LIKES_PROP, is(0))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_VIEW)
        );
    }

    @Test
    void getAllPokemons_defaultParamsAnonymousUser_correctModelAttributesStatusView() throws Exception {
        final var expectedPokemons = pokemonService.getAllPokemons(
            DEF_PAGE_NUM, DEF_PAGE_SIZE, DEF_SORT, DEF_FIELD_TO_SORT, DEF_MATCH
        );
        final var expectedTotalPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedTotalPages, DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(PAGE_NUM_ATTR, is(DEF_PAGE_NUM))
        );
        resultActions.andExpect(
            model().attribute(PAGE_SIZE_ATTR, is(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(SORT_DIR_ATTR, is(DEF_SORT))
        );
        resultActions.andExpect(
            model().attribute(SORT_BY_ATTR, is(DEF_FIELD_TO_SORT))
        );
        resultActions.andExpect(
            model().attribute(MATCH_BY_ATTR, is(DEF_MATCH))
        );
        resultActions.andExpect(
            model().attribute(POKEMONS_ATTR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATTR, is(expectedTotalPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATTR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATTR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATTR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW)
        );
    }

    @Test
    void getAllPokemons_defaultParamsLoggedUser_correctModelAttributesStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );
        final var expectedPokemons = pokemonService.getAllPokemons(
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
            model().attribute(PAGE_NUM_ATTR, is(DEF_PAGE_NUM))
        );
        resultActions.andExpect(
            model().attribute(PAGE_SIZE_ATTR, is(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(SORT_DIR_ATTR, is(DEF_SORT))
        );
        resultActions.andExpect(
            model().attribute(SORT_BY_ATTR, is(DEF_FIELD_TO_SORT))
        );
        resultActions.andExpect(
            model().attribute(MATCH_BY_ATTR, is(DEF_MATCH))
        );
        resultActions.andExpect(
            model().attribute(POKEMONS_ATTR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATTR, is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATTR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATTR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATTR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW)
        );
    }

    @Test
    void getAllPokemons_nonDefaultParamsAnonymousUser_correctModelAttributesStatusView() throws Exception {
        final var expectedPokemons = pokemonService.getAllPokemons(
            NON_DEF_PAGE_NUM, NON_DEF_PAGE_SIZE, NON_DEF_SORT, NON_DEF_FIELD_TO_SORT, NON_DEF_MATCH
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, NON_DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
                .param(PAGE_NUM_REQUEST_PARAM, String.valueOf(NON_DEF_PAGE_NUM))
                .param(PAGE_SIZE_REQUEST_PARAM, String.valueOf(NON_DEF_PAGE_SIZE))
                .param(SORT_DIR_REQUEST_PARAM, NON_DEF_SORT)
                .param(SORT_BY_REQUEST_PARAM, NON_DEF_FIELD_TO_SORT)
                .param(MATCH_BY_REQUEST_PARAM, NON_DEF_MATCH)
        );

        resultActions.andExpect(
            model().attribute(PAGE_NUM_ATTR, is(NON_DEF_PAGE_NUM))
        );
        resultActions.andExpect(
            model().attribute(PAGE_SIZE_ATTR, is(NON_DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(SORT_DIR_ATTR, is(NON_DEF_SORT))
        );
        resultActions.andExpect(
            model().attribute(SORT_BY_ATTR, is(NON_DEF_FIELD_TO_SORT))
        );
        resultActions.andExpect(
            model().attribute(MATCH_BY_ATTR, is(NON_DEF_MATCH))
        );
        resultActions.andExpect(
            model().attribute(POKEMONS_ATTR, hasSize(
                both(greaterThanOrEqualTo(0))
                    .and(lessThanOrEqualTo(NON_DEF_PAGE_SIZE))
            ))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATTR, is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATTR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATTR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATTR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW)
        );
    }

    @Test
    void getAllPokemons_nonDefaultParamsLoggedUser_correctModelAttributesStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );
        final var expectedPokemons = pokemonService.getAllPokemons(
            NON_DEF_PAGE_NUM, NON_DEF_PAGE_SIZE, NON_DEF_SORT, NON_DEF_FIELD_TO_SORT, NON_DEF_MATCH
        );
        final var expectedAllPages = expectedPokemons.getTotalPages();
        final var expectedTotalElements = expectedPokemons.getTotalElements();
        final var expectedPageLimits = PageLimitsCalculator.getPageLimits(expectedAllPages, NON_DEF_PAGE_NUM);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_ALL_MAPPING)
                .param(PAGE_NUM_REQUEST_PARAM, String.valueOf(NON_DEF_PAGE_NUM))
                .param(PAGE_SIZE_REQUEST_PARAM, String.valueOf(NON_DEF_PAGE_SIZE))
                .param(SORT_DIR_REQUEST_PARAM, NON_DEF_SORT)
                .param(SORT_BY_REQUEST_PARAM, NON_DEF_FIELD_TO_SORT)
                .param(MATCH_BY_REQUEST_PARAM, NON_DEF_MATCH)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(
                PAGE_NUM_ATTR, is(NON_DEF_PAGE_NUM)
            ));
        resultActions.andExpect(
            model().attribute(
                PAGE_SIZE_ATTR, is(NON_DEF_PAGE_SIZE)
            ));
        resultActions.andExpect(
            model().attribute(
                SORT_DIR_ATTR, is(NON_DEF_SORT)
            ));
        resultActions.andExpect(
            model().attribute(
                SORT_BY_ATTR, is(NON_DEF_FIELD_TO_SORT)
            ));
        resultActions.andExpect(
            model().attribute(
                MATCH_BY_ATTR, is(NON_DEF_MATCH)
            ));
        resultActions.andExpect(
            model().attribute(POKEMONS_ATTR, hasSize(
                both(greaterThanOrEqualTo(0))
                    .and(lessThanOrEqualTo(NON_DEF_PAGE_SIZE))
            ))
        );
        resultActions.andExpect(
            model().attribute(ALL_PAGES_ATTR, is(expectedAllPages))
        );
        resultActions.andExpect(
            model().attribute(TOTAL_ELEMENTS_ATTR, is(expectedTotalElements))
        );
        resultActions.andExpect(
            model().attribute(PAGE_LEFT_LIMIT_ATTR, is(expectedPageLimits[0]))
        );
        resultActions.andExpect(
            model().attribute(PAGE_RIGHT_LIMIT_ATTR, is(expectedPageLimits[1]))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(DATABASE_VIEW)
        );
    }

    @Test
    @Transactional
    void getTopPokemons_anonymousUser_correctModelAttributeStatusView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        testPokemon.setNumberOfLikes(1);

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_TOP_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATTR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATTR, hasItem(
                hasProperty(NUMBER_OF_LIKES_PROP, is(1))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(RANKING_VIEW)
        );
    }

    @Test
    @Transactional
    void getTopPokemons_loggedUser_correctModelAttributeStatusView() throws Exception {
        final var testPokemon = pokemonService.getPokemonById(TEST_POKEMON_ID);
        testPokemon.setNumberOfLikes(1);
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMONS_GET_TOP_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATTR, hasSize(DEF_PAGE_SIZE))
        );
        resultActions.andExpect(
            model().attribute(TOP_POKEMONS_ATTR, hasItem(
                hasProperty(NUMBER_OF_LIKES_PROP, is(1))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(RANKING_VIEW)
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

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(POKEMON_FAVOURITE_URL)
        );
    }

}
