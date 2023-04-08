package pl.kacperk.pokemonservicefullstack.api.appuser.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacperk.pokemonservicefullstack.ContainerTest;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class AppUserControllerTest extends ContainerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AppUserRepo appUserRepo;
    private AppUser controllerTestUser;

    private static final String CONTROLLER_TEST_USER_NAME = "controllerTestUserName";
    private static final String CONTROLLER_TEST_USER_PASS = "controllerTestUserPass";
    private static final Long TEST_USER_ID = 1L;
    private static final String REGISTER_MAPPING = "/api/users/register";
    private static final String UPDATE_MAPPING = "/api/users/update";
    private static final String USER_NAME = "userName";
    private static final String PASS = "password";
    private static final String MATCHING_PASS = "matchingPassword";
    private static final String LOGIN_URL = "http://localhost/auth/login";
    private static final String REGISTER = "register";
    private static final String REGISTER_SUCCESS = "register-success";
    private static final int ERROR_COUNT = 1;
    private static final String ERROR_MESSAGE = "errorMessage";

    @BeforeEach
    void setUp() throws UserAlreadyExistException {
        TestUtils.prepareAppUserRepoForControllerTest(
            appUserRepo, appUserService, CONTROLLER_TEST_USER_NAME, CONTROLLER_TEST_USER_PASS
        );
        controllerTestUser = appUserService.getAppUserByName(CONTROLLER_TEST_USER_NAME);
    }

    @Test
    void registerAppUser_anonymousUserNoErrors_correctStatusView() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserName = testAppUser.getUserName();
        final var testAppUserPassword = testAppUser.getPassword();

        // when
        final var resultActions = mockMvc.perform(
            post(REGISTER_MAPPING)
                .param(USER_NAME, testAppUserName)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserPassword)
        );

        // then
        final var registeredUser = appUserService.getAppUserByName(testAppUserName);
        assertThat(registeredUser.getPassword())
            .isNotNull();

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_SUCCESS)
        );
    }

    @Test
    void registerAppUser_loggedUserNoErrors_correctStatusView() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserName = testAppUser.getUserName();
        final var testAppUserPassword = testAppUser.getPassword();
        final var sessionWithLoggedUser = TestUtils.getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            post(REGISTER_MAPPING)
                .param(USER_NAME, testAppUserName)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserPassword)
                .session(sessionWithLoggedUser)
        );

        // then
        final var registeredUser = appUserService.getAppUserByName(testAppUserName);
        assertThat(registeredUser.getPassword())
            .isNotNull();

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_SUCCESS)
        );
    }

    @Test
    void registerAppUser_anonymousUserValidationError_correctErrorCountStatusView() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserName = testAppUser.getUserName();
        final var testAppUserPassword = testAppUser.getPassword();

        // when
        final var resultActions = mockMvc.perform(
            post(REGISTER_MAPPING)
                .param(USER_NAME, testAppUserName)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserName)
        );

        // then
        resultActions.andExpect(
            model().errorCount(ERROR_COUNT)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER)
        );
    }

    @Test
    void registerAppUser_loggedUserValidationError_correctErrorCountStatusView() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserName = testAppUser.getUserName();
        final var testAppUserPassword = testAppUser.getPassword();
        final var sessionWithLoggedUser = TestUtils.getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            post(REGISTER_MAPPING)
                .param(USER_NAME, testAppUserName)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserName)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().errorCount(ERROR_COUNT)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER)
        );
    }

    @Test
    void registerAppUser_anonymousUserUserAlreadyExistException_correctModelAttributeStatusView() throws Exception {
        // given
        final var firstDbAppUserName = controllerTestUser.getUserName();
        final var firstDbAppUserPassword = controllerTestUser.getPassword();

        // when
        final var resultActions = mockMvc.perform(
            post(REGISTER_MAPPING)
                .param(USER_NAME, firstDbAppUserName)
                .param(PASS, firstDbAppUserPassword)
                .param(MATCHING_PASS, firstDbAppUserPassword)
        );

        // then
        resultActions.andExpect(
            model().attribute(ERROR_MESSAGE, is(notNullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER)
        );
    }

    @Test
    void registerAppUser_loggedUserUserAlreadyExistException_correctModelAttributeStatusView() throws Exception {
        // given
        final var firstDbAppUserName = controllerTestUser.getUserName();
        final var firstDbAppUserPassword = controllerTestUser.getPassword();
        final var sessionWithLoggedUser = TestUtils.getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            post(REGISTER_MAPPING)
                .param(USER_NAME, firstDbAppUserName)
                .param(PASS, firstDbAppUserPassword)
                .param(MATCHING_PASS, firstDbAppUserPassword)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().attribute(ERROR_MESSAGE, is(notNullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER)
        );
    }

    @Test
    void changeAppUserPassword_anonymousUserNoErrors_correctStatusRedirectedUrl() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserPassword = testAppUser.getPassword();

        // when
        final var resultActions = mockMvc.perform(
            patch(UPDATE_MAPPING)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserPassword)
        );

        // then
        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void changeAppUserPassword_loggedUserNoErrors_correctStatusRedirectedUrl() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserPassword = testAppUser.getPassword();
        final var sessionWithLoggedUser = TestUtils.getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            patch(UPDATE_MAPPING)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserPassword)
                .session(sessionWithLoggedUser)
        );

        // then
        final var firstDbAppUserName = controllerTestUser.getUserName();
        final var firstDbAppUserWithChangedPassword = appUserService.getAppUserByName(firstDbAppUserName);
        assertThat(firstDbAppUserWithChangedPassword.getPassword())
            .isNotEqualTo(controllerTestUser.getPassword());

        resultActions.andExpect(
            model().attribute("loggedUser", is(nullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("account-update-success")
        );
    }

    @Test
    void changeAppUserPassword_anonymousUserValidationError_correctStatusRedirectedUrl() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserName = testAppUser.getUserName();
        final var testAppUserPassword = testAppUser.getPassword();

        // when
        final var resultActions = mockMvc.perform(
            patch(UPDATE_MAPPING)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserName)
        );

        // then
        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void changeAppUserPassword_loggedUserValidationError_correctStatusRedirectedUrl() throws Exception {
        // given
        final var testAppUser = TestUtils.getTestAppUser(TEST_USER_ID);
        final var testAppUserName = testAppUser.getUserName();
        final var testAppUserPassword = testAppUser.getPassword();
        final var sessionWithLoggedUser = TestUtils.getLoggedUserSession(
            controllerTestUser, CONTROLLER_TEST_USER_PASS, mockMvc
        );

        // when
        final var resultActions = mockMvc.perform(
            patch(UPDATE_MAPPING)
                .param(PASS, testAppUserPassword)
                .param(MATCHING_PASS, testAppUserName)
                .session(sessionWithLoggedUser)
        );

        // then
        resultActions.andExpect(
            model().errorCount(1)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name("account-update")
        );
    }

}
