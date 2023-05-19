package pl.kacperk.pokemonservicefullstack.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import pl.kacperk.pokemonservicefullstack.template.AbstractControllerTest;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.MATCHING_PASS_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.PASS_PROP;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTERED_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.REGISTER_REQUEST_DTO;
import static pl.kacperk.pokemonservicefullstack.controller.ControllerTestUtils.getLoggedUserSession;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.ERROR_MSG_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.LOGIN_ERROR_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.LOGIN_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.LOGIN_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.REGISTER_MAPPING;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.REGISTER_REQUEST_DTO_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.REGISTER_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.USERNAME_PASS_COMBINATION_ERROR;

class AuthControllerTest extends AbstractControllerTest {

    private static final String USER_NAME_PROP = "userName";

    @Autowired
    private MockMvc mockMvc;
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
    void getLogin_anonymousUser_correctStatusAndView() throws Exception {
        final var resultActions = mockMvc.perform(
            get(LOGIN_MAPPING)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(LOGIN_VIEW)
        );
    }

    @Test
    void getLogin_loggedUser_correctStatusAndView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(LOGIN_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(LOGIN_VIEW)
        );
    }

    @Test
    void getLoginError_nullAuthenticationException_correctModelAttributeStatusView() throws Exception {
        final var resultActions = mockMvc.perform(
            get(LOGIN_ERROR_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(ERROR_MSG_ATTR, is(nullValue()))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(LOGIN_VIEW)
        );
    }

    @Test
    void getLoginError_notNullAuthenticationException_correctModelAttributeStatusView() throws Exception {
        final var sessionWithAuthExc = new MockHttpSession();
        sessionWithAuthExc.setAttribute(AUTHENTICATION_EXCEPTION, new Object());

        final var resultActions = mockMvc.perform(
            get(LOGIN_ERROR_MAPPING)
                .session(sessionWithAuthExc)
        );

        resultActions.andExpect(
            model().attribute(ERROR_MSG_ATTR, containsString(USERNAME_PASS_COMBINATION_ERROR))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(LOGIN_VIEW)
        );
    }

    @Test
    void getRegister_anonymousUser_correctModelAttributeStatusView() throws Exception {
        final var resultActions = mockMvc.perform(
            get(REGISTER_MAPPING)
        );

        resultActions.andExpect(
            model().attribute(REGISTER_REQUEST_DTO_ATTR, allOf(
                hasProperty(USER_NAME_PROP, nullValue()),
                hasProperty(PASS_PROP, nullValue()),
                hasProperty(MATCHING_PASS_PROP, nullValue())
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW)
        );
    }

    @Test
    void getRegister_loggedUser_correctModelAttributeStatusView() throws Exception {
        final var sessionWithLoggedUser = getLoggedUserSession(
            REGISTERED_USER_NAME, REGISTERED_USER_PASS, mockMvc
        );

        final var resultActions = mockMvc.perform(
            get(REGISTER_MAPPING)
                .session(sessionWithLoggedUser)
        );

        resultActions.andExpect(
            model().attribute(REGISTER_REQUEST_DTO_ATTR, allOf(
                hasProperty(USER_NAME_PROP, nullValue()),
                hasProperty(PASS_PROP, nullValue()),
                hasProperty(MATCHING_PASS_PROP, nullValue())
            ))
        );
        resultActions.andExpect(
            status().isOk()
        );
        resultActions.andExpect(
            view().name(REGISTER_VIEW)
        );
    }

}
