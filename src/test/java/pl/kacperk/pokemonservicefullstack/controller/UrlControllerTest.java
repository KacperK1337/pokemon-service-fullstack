package pl.kacperk.pokemonservicefullstack.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacperk.pokemonservicefullstack.ContainerTest;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.service.PokemonService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.kacperk.pokemonservicefullstack.TestUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.TestUtils.prepareAppUserRepoForControllerTest;
import static pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsMapper.appUserToAppUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UrlControllerTest extends ContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private PokemonService pokemonService;

    private AppUser controllerTestUser;
    private final String controllerTestUserPassword = "controllerTestUserPassword";

    @BeforeEach
    void setUp() throws UserAlreadyExistException {
        final var controllerTestAppUserName = "controllerTestAppUserName";
        prepareAppUserRepoForControllerTest(
                appUserRepo, appUserService, controllerTestAppUserName, controllerTestUserPassword
        );
        controllerTestUser = appUserService.getAppUserByName(controllerTestAppUserName);
    }

    @Test
    void getAbout_anonymousUser_correctStatusView() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get("/about")
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("about")
        );
    }

    @Test
    void getAbout_loggedUser_correctStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get("/about")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("about")
        );
    }

    @Test
    void getAccount_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get("/account")
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
    void getAccount_loggedUser_correctStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get("/account")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("account")
        );
    }

    @Test
    void getAccountUpdate_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get("/account/update")
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
    void getAccountUpdate_loggedUser_correctModelAttributeStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get("/account/update")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "passwordChangeRequestDto", hasProperty(
                                "password", nullValue()
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "passwordChangeRequestDto", hasProperty(
                                "matchingPassword", nullValue()
                        )
                ));
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("account-update")
        );
    }

    @Test
    void getFaqs_anonymousUser_correctStatusView() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get("/faqs")
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("faqs")
        );
    }

    @Test
    void getFaqs_loggedUser_correctStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get("/faqs")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("faqs")
        );
    }

    @Test
    @Transactional
    void getIndex_anonymousUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var firstDbPokemon = pokemonService.getPokemonById(1L);
        final var firstDbAppUserDetails = appUserToAppUserDetails(controllerTestUser);
        pokemonService.addPokemonToFavourites(1L, firstDbAppUserDetails);

        // when
        final var resultActions = mockMvc.perform(
                get("/")
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "numberOfUsers", is(1L)
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "id", is(firstDbPokemon.getId())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "name", is(firstDbPokemon.getName())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "possibleEvolutions", is(firstDbPokemon.getPossibleEvolutions())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "types", is(firstDbPokemon.getTypes())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty("photoUrl", is(firstDbPokemon.getPhotoUrl()))
                ));
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("index")
        );
    }

    @Test
    @Transactional
    void getIndex_loggedUser_correctModelAttributesStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );
        final var firstDbPokemon = pokemonService.getPokemonById(1L);
        final var firstDbAppUserDetails = appUserToAppUserDetails(controllerTestUser);
        pokemonService.addPokemonToFavourites(1L, firstDbAppUserDetails);

        // when
        final var resultActions = mockMvc.perform(
                get("/")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "numberOfUsers", is(1L)
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "id", is(firstDbPokemon.getId())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty("name", is(firstDbPokemon.getName())
                        )
                ));
        resultActions.andExpect(
                model().attribute("topPokemon", hasProperty(
                                "possibleEvolutions", is(firstDbPokemon.getPossibleEvolutions())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "types", is(firstDbPokemon.getTypes())
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "topPokemon", hasProperty(
                                "photoUrl", is(firstDbPokemon.getPhotoUrl())
                        )
                ));
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("index")
        );
    }

    @Test
    void getPokemonFavourite_anonymousUser_correctStatusRedirectedUrl() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get("/pokemon-favourite")
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
    void getPokemonFavourite_loggedUser_correctStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get("/pokemon-favourite")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("pokemon-favourite")
        );
    }

}
