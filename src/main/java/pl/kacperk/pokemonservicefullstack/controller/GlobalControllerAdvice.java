package pl.kacperk.pokemonservicefullstack.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final AppUserService appUserService;

    @ModelAttribute("loggedUser")
    public AppUserResponseDto getLoggedUserResponse(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        AppUser loggedUser = appUserService.getLoggedAppUser(appUserDetails);
        return appUserService.getAppUserAsResponse(loggedUser);
    }
}