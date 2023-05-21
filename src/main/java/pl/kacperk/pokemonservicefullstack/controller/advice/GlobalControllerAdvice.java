package pl.kacperk.pokemonservicefullstack.controller.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    protected static final String LOGGED_USER_ATTR = "loggedUser";

    private final AppUserService appUserService;

    @ModelAttribute(LOGGED_USER_ATTR)
    public AppUserResponseDto getLoggedUserAsResponse(@AuthenticationPrincipal final AppUserDetails userDetails) {
        final AppUser loggedUser = appUserService.getLoggedUser(userDetails);
        return appUserService.getUserAsResponse(loggedUser);
    }
}