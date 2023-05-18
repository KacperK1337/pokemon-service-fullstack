package pl.kacperk.pokemonservicefullstack.controller;

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
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.LOGIN_URL;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTER_REQUEST_DTO;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUserWithId;
import static pl.kacperk.pokemonservicefullstack.controller.AppUserController.ACCOUNT_UPDATE_SUCCESS_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.AppUserController.LOGGED_USER_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.AppUserController.REGISTER_SUCCESS_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.AppUserController.USERS_REGISTER_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.AppUserController.USERS_UPDATE_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.ERROR_MSG_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.REGISTER_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ACCOUNT_UPDATE_VIEW;

class AppUserControllerTest extends AbstractControllerTest {

    private static final String USER_NAME_PARAM = "userName";
    private static final String PASS_PARAM = "password";
    private static final String MATCHING_PASS_PARAM = "matchingPassword";
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
        final var testUserPass = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPass)
                .param(MATCHING_PASS_PARAM, testUserPass)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_SUCCESS_VIEW)
        );
    }

    @Test
    void registerUser_loggedUserNoErrors_correctStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPass = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPass)
                .param(MATCHING_PASS_PARAM, testUserPass)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_SUCCESS_VIEW)
        );
    }

    @Test
    void registerUser_anonymousUserValidationError_correctErrorCountStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPass = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPass)
                .param(MATCHING_PASS_PARAM, testUserName)
        );

        resultActions.andExpect(
            model().errorCount(ERROR_COUNT)
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW)
        );
    }

    @Test
    void registerAppUser_loggedUserValidationError_correctErrorCountStatusView() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPass = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            post(USERS_REGISTER_MAPPING)
                .param(USER_NAME_PARAM, testUserName)
                .param(PASS_PARAM, testUserPass)
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
            view().name(REGISTER_VIEW)
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
            model().attribute(ERROR_MSG_ATTR, is(notNullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW)
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
            model().attribute(ERROR_MSG_ATTR, is(notNullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW)
        );
    }

    @Test
    void changeAppUserPass_anonymousUserNoErrors_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserPass = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPass)
                .param(MATCHING_PASS_PARAM, testUserPass)
        );

        resultActions.andExpect(
            status().is3xxRedirection()
        );
        resultActions.andExpect(
            redirectedUrl(LOGIN_URL)
        );
    }

    @Test
    void changeAppUserPass_loggedUserNoErrors_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserPass = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPass)
                .param(MATCHING_PASS_PARAM, testUserPass)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(LOGGED_USER_ATTR, is(nullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(ACCOUNT_UPDATE_SUCCESS_VIEW)
        );
    }

    @Test
    void changeAppUserPass_anonymousUserValidationError_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPass = testUser.getPassword();

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPass)
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
    void changeAppUserPass_loggedUserValidationError_correctStatusRedirectedUrl() throws Exception {
        final var testUser = createTestAppUserWithId();
        final var testUserName = testUser.getUserName();
        final var testUserPass = testUser.getPassword();
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            patch(USERS_UPDATE_MAPPING)
                .param(PASS_PARAM, testUserPass)
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
            view().name(ACCOUNT_UPDATE_VIEW)
        );
    }

}
