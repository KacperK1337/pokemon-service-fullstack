package pl.kacperk.pokemonservicefullstack;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestUtils {

    private static final String TEST_APP_USER_NAME = "testUserName";
    private static final String TEST_APP_USER_PASS = "testPassword";
    private static final String SECURITY_CONTEXT_ATR = "SPRING_SECURITY_CONTEXT";

    public static AppUser getTestAppUser() {
        return new AppUser(
            AppUserRole.USER, TEST_APP_USER_NAME, TEST_APP_USER_PASS
        );
    }

    public static AppUser getTestAppUser(Long id) {
        final AppUser appUser = new AppUser(
            AppUserRole.USER, TEST_APP_USER_NAME, TEST_APP_USER_PASS
        );
        appUser.setId(id);
        return appUser;
    }

    public static void prepareAppUserRepoForControllerTest(
        final AppUserRepo appUserRepo,
        final AppUserService appUserService,
        final String controllerTestAppUserName,
        final String controllerTestAppUserPassword
    ) throws UserAlreadyExistException {
        appUserRepo.deleteAll();
        final AppUserRegisterRequestDto registerRequestDto = new AppUserRegisterRequestDto();
        registerRequestDto.setUserName(controllerTestAppUserName);
        registerRequestDto.setPassword(controllerTestAppUserPassword);
        registerRequestDto.setMatchingPassword(controllerTestAppUserPassword);
        appUserService.registerAppUser(registerRequestDto);
    }

    public static MockHttpSession getLoggedUserSession(
        final AppUser appUserInDb,
        final String appUserInDbPassword,
        final MockMvc mockMvc
    ) throws Exception {
        final String userName = appUserInDb.getUserName();

        final ResultActions resultLoginActions = mockMvc.perform(
            post("/auth/login")
                .param("username", userName)
                .param("password", appUserInDbPassword)
        );
        final SecurityContext securityContext = (SecurityContext) resultLoginActions
            .andReturn()
            .getRequest()
            .getSession()
            .getAttribute(SECURITY_CONTEXT_ATR);
        final MockHttpSession sessionWithLoggedUser = new MockHttpSession();
        sessionWithLoggedUser.setAttribute(SECURITY_CONTEXT_ATR, securityContext);
        return sessionWithLoggedUser;
    }

}
