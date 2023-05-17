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

    protected static final String ABOUT_MAPPING = "/about";
    protected static final String ACCOUNT_MAPPING = "/account";
    protected static final String ACCOUNT_UPDATE_MAPPING = "/account/update";
    protected static final String FAQS_MAPPING = "/faqs";
    protected static final String INDEX_MAPPING = "/";
    protected static final String POKEMON_FAVOURITE_MAPPING = "/pokemon-favourite";

    protected static final String PASS_CHANGE_REQUEST_DTO_ATTR = "passwordChangeRequestDto";
    protected static final String NUMBER_OF_USERS_ATTR = "numberOfUsers";
    protected static final String TOP_POKEMON_ATTR = "topPokemon";

    protected static final String ABOUT_VIEW = "about";
    protected static final String ACCOUNT_VIEW = "account";
    protected static final String ACCOUNT_UPDATE_VIEW = "account-update";
    protected static final String FAQS_VIEW = "faqs";
    protected static final String INDEX_VIEW = "index";
    protected static final String POKEMON_FAVOURITE_VIEW = "pokemon-favourite";

    private final AppUserService appUserService;
    private final PokemonService pokemonService;

    @GetMapping(ABOUT_MAPPING)
    public String getAbout() {
        return ABOUT_VIEW;
    }

    @GetMapping(ACCOUNT_MAPPING)
    public String getAccount() {
        return ACCOUNT_VIEW;
    }

    @GetMapping(ACCOUNT_UPDATE_MAPPING)
    public String getAccountUpdate(final Model model) {
        final AppUserPasswordChangeRequestDto passChangeRequestDto = new AppUserPasswordChangeRequestDto();
        model.addAttribute(PASS_CHANGE_REQUEST_DTO_ATTR, passChangeRequestDto);
        return ACCOUNT_UPDATE_VIEW;
    }

    @GetMapping(FAQS_MAPPING)
    public String getFaqs() {
        return FAQS_VIEW;
    }

    @GetMapping(INDEX_MAPPING)
    public String getIndex(final Model model) {
        model.addAttribute(NUMBER_OF_USERS_ATTR, appUserService.getNumberOfUsers());
        model.addAttribute(TOP_POKEMON_ATTR, pokemonService.getTopPokemon());
        return INDEX_VIEW;
    }

    @GetMapping(POKEMON_FAVOURITE_MAPPING)
    public String getPokemonFavourite() {
        return POKEMON_FAVOURITE_VIEW;
    }
}
