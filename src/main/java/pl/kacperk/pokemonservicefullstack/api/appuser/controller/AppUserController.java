package pl.kacperk.pokemonservicefullstack.api.appuser.controller;

import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    public String registerAppUser(
            @ModelAttribute("registerRequestDto") @Valid AppUserRegisterRequestDto registerRequestDto,
            BindingResult result,
            final Model model
    ) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            appUserService.registerAppUser(registerRequestDto);
        } catch (UserAlreadyExistException uaeEx) {
            model.addAttribute("errorMessage", uaeEx.getMessage());
            return "register";
        }
        return "register-success";
    }

    @PatchMapping("/update")
    public String changeAppUserPassword(
            @AuthenticationPrincipal AppUserDetails details,
            @ModelAttribute("passwordChangeRequestDto") @Valid AppUserPasswordChangeRequestDto passwordChangeRequestDto,
            BindingResult result,
            final Model model
    ) {
        if (result.hasErrors()) {
            return "account-update";
        }
        try {
            appUserService.changeLoggedUserPassword(details, passwordChangeRequestDto);
        } catch (ServletException sEx) {
            model.addAttribute("errorMessage", sEx.getMessage());
            return "account-update";
        }
        model.addAttribute("loggedUser", null);
        return "account-update-success";
    }

}
