package pl.kacperk.pokemonservicefullstack.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;

@Controller
@RequiredArgsConstructor
public class AuthController {

    protected static final String LOGIN_MAPPING = "/auth/login";
    protected static final String LOGIN_ERROR_MAPPING = "/auth/login-error";
    protected static final String REGISTER_MAPPING = "/auth/register";

    protected static final String ERROR_MSG_ATTR = "errorMessage";
    protected static final String REGISTER_REQUEST_DTO_ATTR = "registerRequestDto";

    protected static final String LOGIN_VIEW = "login";
    protected static final String REGISTER_VIEW = "register";

    protected static final String USERNAME_PASS_COMBINATION_ERROR = "Invalid username and password combination";

    @GetMapping(LOGIN_MAPPING)
    public String getLogin() {
        return LOGIN_VIEW;
    }

    @GetMapping(LOGIN_ERROR_MAPPING)
    public String getLoginError(final HttpSession session, final Model model) {
        String errorMessage = null;
        if (session != null) {
            final Object exception = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (exception != null) {
                errorMessage = USERNAME_PASS_COMBINATION_ERROR;
            }
        }
        model.addAttribute(ERROR_MSG_ATTR, errorMessage);
        return LOGIN_VIEW;
    }

    @GetMapping(REGISTER_MAPPING)
    public String getRegister(final Model model) {
        model.addAttribute(REGISTER_REQUEST_DTO_ATTR, new AppUserRegisterRequestDto());
        return REGISTER_VIEW;
    }

}
