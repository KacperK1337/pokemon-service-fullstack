package pl.kacperk.pokemonservicefullstack.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/login-error")
    public String getLoginError(HttpSession session, Model model) {
        String errorMessage = null;
        if (session != null) {
            Object exception = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (exception != null) {
                errorMessage = "Invalid username and password combination";
            }
        }
        model.addAttribute("errorMessage", errorMessage);
        return "login";
    }

    @GetMapping("/register")
    public String getRegister(Model model) {
        AppUserRegisterRequestDto registerRequestDto = new AppUserRegisterRequestDto();
        model.addAttribute("registerRequestDto", registerRequestDto);
        return "register";
    }
}
