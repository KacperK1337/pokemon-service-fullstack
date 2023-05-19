package pl.kacperk.pokemonservicefullstack.controller;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ControllerTestUtils {

    protected static final String REGISTERED_USER_NAME = "registeredUserName";
    protected static final String REGISTERED_USER_PASS = "registeredUserPass";
    protected static final AppUserRegisterRequestDto REGISTER_REQUEST_DTO = new AppUserRegisterRequestDto(
        REGISTERED_USER_NAME, REGISTERED_USER_PASS, REGISTERED_USER_PASS
    );

    protected static final String PASS_PROP = "password";
    protected static final String MATCHING_PASS_PROP = "matchingPassword";
    protected static final String ID_PROP = "id";
    protected static final String NAME_PROP = "name";
    protected static final String EVOLUTIONS_PROP = "evolutions";
    protected static final String PHOTO_URL_PROP = "photoUrl";

    protected static final String LOGIN_URL = "http://localhost/auth/login";

    private static final String LOGIN_MAPPING = "/auth/login";
    private static final String USERNAME_PARAM = "username";
    private static final String PASS_PARAM = "password";
    private static final String SECURITY_CONTEXT_ATTR = "SPRING_SECURITY_CONTEXT";

    protected static MockHttpSession getLoggedUserSession(
        final String registeredAppUserName, final String registeredAppUserPass,
        final MockMvc mockMvc
    ) throws Exception {
        final ResultActions resultLoginActions = mockMvc.perform(
            post(LOGIN_MAPPING)
                .param(USERNAME_PARAM, registeredAppUserName)
                .param(PASS_PARAM, registeredAppUserPass)
        );
        final SecurityContext securityContext = (SecurityContext) resultLoginActions
            .andReturn()
            .getRequest()
            .getSession()
            .getAttribute(SECURITY_CONTEXT_ATTR);
        final MockHttpSession sessionWithLoggedUser = new MockHttpSession();
        sessionWithLoggedUser.setAttribute(SECURITY_CONTEXT_ATTR, securityContext);
        return sessionWithLoggedUser;
    }

}
