package pl.kacperk.pokemonservicefullstack.entity.appuser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppUserPermission {

    POKEMONS_LIKE("pokemons:like");

    private final String permission;

}
