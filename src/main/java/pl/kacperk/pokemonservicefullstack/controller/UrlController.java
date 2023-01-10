package pl.kacperk.pokemonservicefullstack.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.service.AppUserService;
import pl.kacperk.pokemonservicefullstack.api.pokemon.service.PokemonService;

@Controller
@RequiredArgsConstructor
public class UrlController {

    private final AppUserService appUserService;
    private final PokemonService pokemonService;

    @GetMapping("/about")
    public String getAbout() {
        return "about";
    }

    @GetMapping("/account")
    public String getAccount() {
        return "account";
    }

    @GetMapping("/account/update")
    public String getAccountUpdate(Model model) {
        AppUserPasswordChangeRequestDto passwordChangeRequestDto = new AppUserPasswordChangeRequestDto();
        model.addAttribute("passwordChangeRequestDto", passwordChangeRequestDto);
        return "account-update";
    }

    @GetMapping("/faqs")
    public String getFaqs() {
        return "faqs";
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("numberOfUsers", appUserService.getNumberOfUsers());
        model.addAttribute("topPokemon", pokemonService.getTopPokemon());
        return "index";
    }

    @GetMapping("/pokemon-favourite")
    public String getPokemonFavourite() {
        return "pokemon-favourite";
    }
}
