package pl.kacperk.pokemonservicefullstack.api.appuser.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacperk.pokemonservicefullstack.AbstractControllerTest;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.ACCOUNT_UPDATE_VIEW_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.ERROR_MESSAGE_ATR;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.LOGIN_URL;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.PASS_PARAM;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTERED_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTERED_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTER_REQUEST_DTO;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.REGISTER_VIEW_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ControllerUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUserWithId;

class AppUserControllerTest extends AbstractControllerTest {

    private static final String USERS_REGISTER_MAPPING = "/api/users/register";
    private static final String USERS_UPDATE_MAPPING = "/api/users/update";

    private static final String USER_NAME_PARAM = "userName";
    private static final String MATCHING_PASS_PARAM = "matchingPassword";

    private static final String LOGGED_USER_ATR = "loggedUser";

    private static final String REGISTER_SUCCESS_VIEW_NAME = "register-success";
    private static final String ACCOUNT_UPDATE_SUCCESS_VIEW_NAME = "account-update-success";

    private static final int ERROR_COUNT = 1;

    @Autowired
    private MockMvc mockMvc;
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
    void registerUser_anonymousUserNoErrors_correctStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPassword = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserPassword)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_SUCCESS_VIEW_NAME)
        );
    }

    @Test
    void registerUser_loggedUserNoErrors_correctStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPassword = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserPassword)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_SUCCESS_VIEW_NAME)
        );
    }

    @Test
    void registerUser_anonymousUserValidationError_correctErrorCountStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPassword = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserName)
        );

        resultActions.andExpect(
            model().errorCount(ERROR_COUNT)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW_NAME)
        );
    }

    @Test
    void registerAppUser_loggedUserValidationError_correctErrorCountStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPassword = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserName)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().errorCount(ERROR_COUNT)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW_NAME)
        );
    }

    @Test
    void registerAppUser_anonymousUserUserAlreadyExistException_correctModelAttributeStatusView() throws Exception {
        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, REGISTERED_USER_NAME)
                .param(PASS_PARAM, REGISTERED_USER_PASS)
                .param(MATCHING_PASS_PARAM, REGISTERED_USER_PASS)
        );

        resultActions.andExpect(
            model().attribute(ERROR_MESSAGE_ATR, is(notNullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW_NAME)
        );
    }

    @Test
    void registerAppUser_loggedUserUserAlreadyExistException_correctModelAttributeStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, REGISTERED_USER_NAME)
                .param(PASS_PARAM, REGISTERED_USER_PASS)
                .param(MATCHING_PASS_PARAM, REGISTERED_USER_PASS)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(ERROR_MESSAGE_ATR, is(notNullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW_NAME)
        );
    }

    @Test
    void changeAppUserPassword_anonymousUserNoErrors_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserPassword = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserPassword)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void changeAppUserPassword_loggedUserNoErrors_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserPassword = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserPassword)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(LOGGED_USER_ATR, is(nullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ACCOUNT_UPDATE_SUCCESS_VIEW_NAME)
        );
    }

    @Test
    void changeAppUserPassword_anonymousUserValidationError_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPassword = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserName)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void changeAppUserPassword_loggedUserValidationError_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPassword = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPassword)
                .param(MATCHING_PASS_PARAM, testUserName)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().errorCount(ERROR_COUNT)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ACCOUNT_UPDATE_VIEW_NAME)
        );
    }

}
