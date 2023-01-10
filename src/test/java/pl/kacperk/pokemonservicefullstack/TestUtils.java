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

    public static AppUser getTestAppUser() {
        AppUser appUser = new AppUser(AppUserRole.USER, "testUserName", "testPassword");
        appUser.setId(1L);
        return appUser;
    }

    public static void prepareAppUserRepoForControllerTest(
            AppUserRepo appUserRepo, AppUserService appUserService,
            String controllerTestAppUserName, String controllerTestAppUserPassword
    )
            throws UserAlreadyExistException {
        appUserRepo.deleteAll();
        AppUserRegisterRequestDto appUserRegisterRequestDto = new AppUserRegisterRequestDto();
        appUserRegisterRequestDto.setUserName(controllerTestAppUserName);
        appUserRegisterRequestDto.setPassword(controllerTestAppUserPassword);
        appUserRegisterRequestDto.setMatchingPassword(controllerTestAppUserPassword);
        appUserService.registerAppUser(appUserRegisterRequestDto);
    }

    public static MockHttpSession getLoggedUserSession(
            AppUser appUserInDb, String appUserInDbPassword, MockMvc mockMvc
    ) throws Exception {
        String appUserName = appUserInDb.getUserName();

        ResultActions resultLoginActions = mockMvc.perform(post("/auth/login")
                .param("username", appUserName)
                .param("password", appUserInDbPassword)
        );
        SecurityContext securityContext = (SecurityContext) resultLoginActions
                .andReturn()
                .getRequest()
                .getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        MockHttpSession sessionWithLoggedUser = new MockHttpSession();
        sessionWithLoggedUser.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        return sessionWithLoggedUser;
    }
}
