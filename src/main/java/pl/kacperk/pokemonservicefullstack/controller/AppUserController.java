package pl.kacperk.pokemonservicefullstack.controller;

import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static pl.kacperk.pokemonservicefullstack.controller.AuthController.ERROR_MSG_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.REGISTER_REQUEST_DTO_ATTR;
import static pl.kacperk.pokemonservicefullstack.controller.AuthController.REGISTER_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.ACCOUNT_UPDATE_VIEW;
import static pl.kacperk.pokemonservicefullstack.controller.UrlController.PASS_CHANGE_REQUEST_DTO_ATTR;

@Controller
@RequiredArgsConstructor
public class AppUserController {

    protected static final String USERS_REGISTER_MAPPING = "/api/users/register";
    protected static final String USERS_UPDATE_MAPPING = "/api/users/update";

    protected static final String LOGGED_USER_ATTR = "loggedUser";

    protected static final String REGISTER_SUCCESS_VIEW = "register-success";
    protected static final String ACCOUNT_UPDATE_SUCCESS_VIEW = "account-update-success";

    private final AppUserService appUserService;

    @PostMapping(USERS_REGISTER_MAPPING)
    public String registerAppUser(
        @ModelAttribute(REGISTER_REQUEST_DTO_ATTR)
        @Valid final AppUserRegisterRequestDto registerRequestDto,
        final BindingResult result,
        final Model model
    ) {
        if (result.hasErrors()) {
            return REGISTER_VIEW;
        }

        try {
            appUserService.registerAppUser(registerRequestDto);
        } catch (UserAlreadyExistException uaeEx) {
            model.addAttribute(ERROR_MSG_ATTR, uaeEx.getMessage());
            return REGISTER_VIEW;
        }

        return REGISTER_SUCCESS_VIEW;
    }

    @PatchMapping(USERS_UPDATE_MAPPING)
    public String changeAppUserPassword(
        @AuthenticationPrincipal final AppUserDetails details,
        @ModelAttribute(PASS_CHANGE_REQUEST_DTO_ATTR)
        @Valid final AppUserPasswordChangeRequestDto passChangeRequestDto,
        final BindingResult result,
        final Model model
    ) {
        if (result.hasErrors()) {
            return ACCOUNT_UPDATE_VIEW;
        }

        try {
            appUserService.changeLoggedUserPassword(details, passChangeRequestDto);
        } catch (ServletException sEx) {
            model.addAttribute(ERROR_MSG_ATTR, sEx.getMessage());
            return ACCOUNT_UPDATE_VIEW;
        }
        model.addAttribute(LOGGED_USER_ATTR, null);

        return ACCOUNT_UPDATE_SUCCESS_VIEW;
    }

}
