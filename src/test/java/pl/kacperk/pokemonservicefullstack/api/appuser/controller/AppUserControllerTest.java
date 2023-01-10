package pl.kacperk.pokemonservicefullstack.api.appuser.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepo appUserRepo;

    private AppUser controllerTestUser;
    private final String requestMappingUrl = "/api/users";
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
    void registerAppUser_withAnonymousUserAndNoErrors_correctStatusAndView() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserName = testAppUser.getUserName();
        String testAppUserPassword = testAppUser.getPassword();

        // when
        ResultActions resultActions = mockMvc.perform(post(requestMappingUrl + "/register")
                .param("userName", testAppUserName)
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserPassword)
        );

        // then
        AppUser registeredUser = appUserService.getAppUserByName(testAppUserName);
        assertThat(registeredUser.getPassword()).isNotNull();

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register-success"));
    }

    @Test
    void registerAppUser_withLoggedUserAndNoErrors_correctStatusAndView() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserName = testAppUser.getUserName();
        String testAppUserPassword = testAppUser.getPassword();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(post(requestMappingUrl + "/register")
                .param("userName", testAppUserName)
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserPassword)
                .session(sessionWithLoggedUser)
        );

        // then
        AppUser registeredUser = appUserService.getAppUserByName(testAppUserName);
        assertThat(registeredUser.getPassword()).isNotNull();

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register-success"));
    }

    @Test
    void registerAppUser_withAnonymousUserAndValidationError_correctErrorCountAndStatusAndView() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserName = testAppUser.getUserName();
        String testAppUserPassword = testAppUser.getPassword();

        // when
        ResultActions resultActions = mockMvc.perform(post(requestMappingUrl + "/register")
                .param("userName", testAppUserName)
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserName)
        );

        // then
        resultActions.andExpect(model().errorCount(1));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register"));
    }

    @Test
    void registerAppUser_withLoggedUserAndValidationError_correctErrorCountAndStatusAndView() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserName = testAppUser.getUserName();
        String testAppUserPassword = testAppUser.getPassword();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(post(requestMappingUrl + "/register")
                .param("userName", testAppUserName)
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserName)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().errorCount(1));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register"));
    }

    @Test
    void registerAppUser_withAnonymousUserAndUserAlreadyExistException_correctModelAttributeAndStatusAndView()
            throws Exception {
        // given
        String firstDbAppUserName = controllerTestUser.getUserName();
        String firstDbAppUserPassword = controllerTestUser.getPassword();

        // when
        ResultActions resultActions = mockMvc.perform(post(requestMappingUrl + "/register")
                .param("userName", firstDbAppUserName)
                .param("password", firstDbAppUserPassword)
                .param("matchingPassword", firstDbAppUserPassword)
        );

        // then
        resultActions.andExpect(model().attribute("errorMessage", is(notNullValue())));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register"));
    }

    @Test
    void registerAppUser_withLoggedUserAndUserAlreadyExistException_correctModelAttributeAndStatusAndView()
            throws Exception {
        // given
        String firstDbAppUserName = controllerTestUser.getUserName();
        String firstDbAppUserPassword = controllerTestUser.getPassword();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(post(requestMappingUrl + "/register")
                .param("userName", firstDbAppUserName)
                .param("password", firstDbAppUserPassword)
                .param("matchingPassword", firstDbAppUserPassword)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().attribute("errorMessage", is(notNullValue())));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("register"));
    }

    @Test
    void changeAppUserPassword_withAnonymousUserAndNoErrors_correctStatusAndRedirectedUrl() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserPassword = testAppUser.getPassword();

        // when
        ResultActions resultActions = mockMvc.perform(patch(requestMappingUrl + "/update")
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserPassword)
        );

        // then
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    void changeAppUserPassword_withLoggedUserAndNoErrors_correctStatusAndRedirectedUrl() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserPassword = testAppUser.getPassword();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(patch(requestMappingUrl + "/update")
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserPassword)
                .session(sessionWithLoggedUser)
        );

        // then
        String firstDbAppUserName = controllerTestUser.getUserName();
        AppUser firstDbAppUserWithChangedPassword = appUserService.getAppUserByName(firstDbAppUserName);
        assertThat(firstDbAppUserWithChangedPassword.getPassword())
                .isNotEqualTo(controllerTestUser.getPassword());

        resultActions.andExpect(model().attribute("loggedUser", is(nullValue())));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("account-update-success"));
    }

    @Test
    void changeAppUserPassword_withAnonymousUserAndValidationError_correctStatusAndRedirectedUrl() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserName = testAppUser.getUserName();
        String testAppUserPassword = testAppUser.getPassword();

        // when
        ResultActions resultActions = mockMvc.perform(patch(requestMappingUrl + "/update")
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserName)
        );

        // then
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    void changeAppUserPassword_withLoggedUserAndValidationError_correctStatusAndRedirectedUrl() throws Exception {
        // given
        AppUser testAppUser = TestUtils.getTestAppUser();
        String testAppUserName = testAppUser.getUserName();
        String testAppUserPassword = testAppUser.getPassword();
        MockHttpSession sessionWithLoggedUser =
                TestUtils.getLoggedUserSession(controllerTestUser, controllerTestUserPassword, mockMvc);

        // when
        ResultActions resultActions = mockMvc.perform(patch(requestMappingUrl + "/update")
                .param("password", testAppUserPassword)
                .param("matchingPassword", testAppUserName)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(model().errorCount(1));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("account-update"));
    }

}
