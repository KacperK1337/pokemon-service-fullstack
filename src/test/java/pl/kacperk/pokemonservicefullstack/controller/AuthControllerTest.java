package pl.kacperk.pokemonservicefullstack.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacperk.pokemonservicefullstack.ContainerTest;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.kacperk.pokemonservicefullstack.TestUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.TestUtils.prepareAppUserRepoForControllerTest;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends ContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepo appUserRepo;

    private AppUser controllerTestUser;
    private final String requestMappingUrl = "/auth";
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
    void getLogin_anonymousUser_correctStatusAndView() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get(requestMappingUrl + "/login")
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("login")
        );
    }

    @Test
    void getLogin_loggedUser_correctStatusAndView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get(requestMappingUrl + "/login")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("login")
        );
    }

    @Test
    void getLoginError_nullAuthenticationException_correctModelAttributeStatusView() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get(requestMappingUrl + "/login-error")
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "errorMessage", nullValue()
                ));
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("login")
        );
    }

    @Test
    void getLoginError_notNullAuthenticationException_correctModelAttributeStatusView() throws Exception {
        // given
        final var sessionWithAuthenticationException = new MockHttpSession();
        sessionWithAuthenticationException.setAttribute(
                WebAttributes.AUTHENTICATION_EXCEPTION, new Object()
        );

        // when
        final var resultActions = mockMvc.perform(
                get(requestMappingUrl + "/login-error")
                        .session(sessionWithAuthenticationException)
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "errorMessage", containsString("Invalid username and password combination")
                )
        );
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("login")
        );
    }

    @Test
    void getRegister_anonymousUser_correctModelAttributeStatusView() throws Exception {
        // when
        final var resultActions = mockMvc.perform(
                get(requestMappingUrl + "/register")
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "registerRequestDto", hasProperty(
                                "userName", nullValue()
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "registerRequestDto", hasProperty(
                                "password", nullValue()
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "registerRequestDto", hasProperty(
                                "matchingPassword", nullValue()
                        )
                ));
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("register")
        );
    }

    @Test
    void getRegister_loggedUser_correctModelAttributeStatusView() throws Exception {
        // given
        final var sessionWithLoggedUser = getLoggedUserSession(
                controllerTestUser, controllerTestUserPassword, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
                get(requestMappingUrl + "/register")
                        .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
                model().attribute(
                        "registerRequestDto", hasProperty(
                                "userName", nullValue()
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "registerRequestDto", hasProperty(
                                "password", nullValue()
                        )
                ));
        resultActions.andExpect(
                model().attribute(
                        "registerRequestDto", hasProperty(
                                "matchingPassword", nullValue()
                        )
                ));
        resultActions.andExpect(
                status().isOk()
        );
        resultActions.andExpect(
                view().name("register")
        );
    }

}
