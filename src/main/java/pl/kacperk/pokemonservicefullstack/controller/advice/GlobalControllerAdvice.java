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

    private final AppUserService appUserService;

    @ModelAttribute("loggedUser")
    public AppUserResponseDto getLoggedUserResponse(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        AppUser loggedUser = appUserService.getLoggedUser(appUserDetails);
        return appUserService.getUserAsResponse(loggedUser);
    }
}