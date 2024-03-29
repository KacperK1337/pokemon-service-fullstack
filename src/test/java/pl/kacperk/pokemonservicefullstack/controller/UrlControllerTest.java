package pl.kacperk.pokemonservicefullstack.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacperk.pokemonservicefullstack.template.AbstractControllerTest;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.ID_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.LOGIN_URL;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.MATCHING_PASS_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.NAME_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.PASS_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.PHOTO_URL_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.EVOLUTIONS_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTER_REQUEST_DTO;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ABOUT_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ABOUT_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ACCOUNT_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ACCOUNT_UPDATE_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ACCOUNT_UPDATE_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ACCOUNT_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.FAQS_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.FAQS_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.INDEX_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.INDEX_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.NUMBER_OF_USERS_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.PASS_CHANGE_REQUEST_DTO_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.POKEMON_FAVOURITE_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.POKEMON_FAVOURITE_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.TOP_POKEMON_ATTR;
import static pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsMapper.userToDetails;

class UrlControllerTest extends AbstractControllerTest {

    private static final String TYPES_PROP = "types";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserService userService;
    @Autowired
    private AppUserRepo userRepo;
    @Autowired
    private PokemonService pokemonService;

    @BeforeEach
    void setUp() throws UserAlreadyExistException {
        userRepo.deleteAll();
        userService.registerUser(REGISTER_REQUEST_DTO);
    }

    @Test
    void getAbout_anonymousUser_correctStatusView() throws Exception {
        final var resultActions = mockMvc.perform(
            get(ABOUT_MAPPING)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ABOUT_VIEW)
        );
    }

    @Test
    void getAbout_loggedUser_correctStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(ABOUT_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ABOUT_VIEW)
        );
    }

    @Test
    void getAccount_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        final var resultActions = mockMvc.perform(
            get(ACCOUNT_MAPPING)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void getAccount_loggedUser_correctStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(ACCOUNT_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ACCOUNT_VIEW)
        );
    }

    @Test
    void getAccountUpdate_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        final var resultActions = mockMvc.perform(
            get(ACCOUNT_UPDATE_MAPPING)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void getAccountUpdate_loggedUser_correctModelAttributeStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(ACCOUNT_UPDATE_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(PASS_CHANGE_REQUEST_DTO_ATTR, allOf(
                hasProperty(PASS_PROP, nullValue()),
                hasProperty(MATCHING_PASS_PROP, nullValue())
            ))
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ACCOUNT_UPDATE_VIEW)
        );
    }

    @Test
    void getFaqs_anonymousUser_correctStatusView() throws Exception {
        final var resultActions = mockMvc.perform(
            get(FAQS_MAPPING)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(FAQS_VIEW)
        );
    }

    @Test
    void getFaqs_loggedUser_correctStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(FAQS_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(FAQS_VIEW)
        );
    }

    @Test
    @Transactional
    void getIndex_anonymousUser_correctModelAttributesStatusView() throws Exception {
        final var registeredPokemonId = 3L;
        final var registeredPokemon = pokemonService.getPokemonById(registeredPokemonId);
        final var registeredUser = userService.getUserByName(REGISTERED_USER_NAME);
        final var registeredUserDetails = userToDetails(registeredUser);
        pokemonService.addPokemonToFavourites(registeredPokemonId, registeredUserDetails);

        final var resultActions = mockMvc.perform(
            get(INDEX_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(
                NUMBER_OF_USERS_ATTR, is(1L)
            ));
        resultActions.andExpect(
            model().attribute(TOP_POKEMON_ATTR, allOf(
                hasProperty(ID_PROP, is(registeredPokemonId)),
                hasProperty(NAME_PROP, is(registeredPokemon.getName())),
                hasProperty(EVOLUTIONS_PROP, is(registeredPokemon.getEvolutions())),
                hasProperty(TYPES_PROP, is(registeredPokemon.getTypes())),
                hasProperty(PHOTO_URL_PROP, is(registeredPokemon.getPhotoUrl()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(INDEX_VIEW)
        );
    }

    @Test
    @Transactional
    void getIndex_loggedUser_correctModelAttributesStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );
        final var registeredPokemonId = 3L;
        final var registeredPokemon = pokemonService.getPokemonById(registeredPokemonId);
        final var registeredUser = userService.getUserByName(REGISTERED_USER_NAME);
        final var registeredUserDetails = userToDetails(registeredUser);
        pokemonService.addPokemonToFavourites(registeredPokemonId, registeredUserDetails);

        final var resultActions = mockMvc.perform(
            get(INDEX_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(
                NUMBER_OF_USERS_ATTR, is(1L)
            ));
        resultActions.andExpect(
            model().attribute(TOP_POKEMON_ATTR, allOf(
                hasProperty(ID_PROP, is(registeredPokemonId)),
                hasProperty(NAME_PROP, is(registeredPokemon.getName())),
                hasProperty(EVOLUTIONS_PROP, is(registeredPokemon.getEvolutions())),
                hasProperty(TYPES_PROP, is(registeredPokemon.getTypes())),
                hasProperty(PHOTO_URL_PROP, is(registeredPokemon.getPhotoUrl()))
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(INDEX_VIEW)
        );
    }

    @Test
    void getPokemonFavourite_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        final var resultActions = mockMvc.perform(
            get(POKEMON_FAVOURITE_MAPPING)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void getPokemonFavourite_loggedUser_correctStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(POKEMON_FAVOURITE_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(POKEMON_FAVOURITE_VIEW)
        );
    }

}
