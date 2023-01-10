package pl.kacperk.pokemonservicefullstack.api.appuser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppUserPermission {

    POKEMONS_LIKE("pokemons:like");

    private final String permission;
}
