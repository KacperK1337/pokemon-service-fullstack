package pl.kacperk.pokemonservicefullstack.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.kacperk.pokemonservicefullstack.TestUtils;
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

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

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
        String controllerTestAppUserName = "controllerTestAppUserName";
        TestUtils.prepareAppUserRepoForControllerTest(
                appUserRepo, appUserService, controllerTestAppUserName, controllerTestUserPassword
        );
        controllerTestUser = appUserService.getAppUserByName(controllerTestAppUserName);
    }

    @Test
    void getLogin_withAnonymousUser_correctStatusAndView() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/login"));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("login"));
    }

    @Test
    void getLogin_withLoggedUser_correctStatusAndView() throws Exception {
        // given
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/login")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("login"));
    }

    @Test
    void getLoginError_nullAUTHENTICATION_EXCEPTION_correctModelAttributeAndStatusAndView() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/login-error"));

        // then
        resultActions.andExpect(model().attribute("errorMessage", nullValue()));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("login"));
    }

    @Test
    void getLoginError_notNullAUTHENTICATION_EXCEPTION_correctModelAttributeAndStatusAndView() throws Exception {
        // given
        MockHttpSession sessionWithAuthenticationException = new MockHttpSession();
        sessionWithAuthenticationException.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new Object());

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/login-error")
                .session(sessionWithAuthenticationException));

        // then
        resultActions.andExpect(model().attribute("errorMessage",
                containsString("Invalid username and password combination")
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("login"));
    }

    @Test
    void getRegister_withAnonymousUser_correctModelAttributeAndStatusAndView() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/register"));

        // then
        resultActions.andExpect(model().attribute("registerRequestDto",
                hasProperty("userName", nullValue())
        ));
        resultActions.andExpect(model().attribute("registerRequestDto",
                hasProperty("password", nullValue())
        ));
        resultActions.andExpect(model().attribute("registerRequestDto",
                hasProperty("matchingPassword", nullValue())
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register"));
    }

    @Test
    void getRegister_withLoggedUser_correctModelAttributeAndStatusAndView() throws Exception {
        // given
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(get(requestMappingUrl + "/register")
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("registerRequestDto",
                hasProperty("userName", nullValue())
        ));
        resultActions.andExpect(model().attribute("registerRequestDto",
                hasProperty("password", nullValue())
        ));
        resultActions.andExpect(model().attribute("registerRequestDto",
                hasProperty("matchingPassword", nullValue())
        ));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register"));
    }
}