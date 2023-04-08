package pl.kacperk.pokemonservicefullstack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.api.pokemon.model.Pokemon;
import pl.kacperk.pokemonservicefullstack.api.pokemon.repo.PokemonRepo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DbDataLoaderTest {

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private PokemonRepo pokemonRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DbDataLoader underTest;

    @BeforeEach
    void setUp() {
        underTest = new DbDataLoader(appUserRepo, pokemonRepo, passwordEncoder);
    }

    @Test
    void start_staticDbResourcesNotEmpty_resourcesSavedToRepo() {
        // when
        underTest.start();

        // then
        verify(passwordEncoder, times(2)).encode(any());

        final var appUserArgumentCaptor = forClass(AppUser.class);
        final var pokemonArgumentCaptor = forClass(Pokemon.class);
        verify(appUserRepo, times(2))
                .save(appUserArgumentCaptor.capture());
        verify(pokemonRepo, times(905))
                .save(pokemonArgumentCaptor.capture());
        final var capturedAppUser = appUserArgumentCaptor.getValue();
        final var capturedPokemon = pokemonArgumentCaptor.getValue();

        assertThat(capturedAppUser.getRole())
                .isEqualTo(AppUserRole.USER);
        assertThat(capturedAppUser.getUserName())
                .isNotEmpty();

        assertThat(capturedPokemon.getName())
                .isNotEmpty();
        assertThat(capturedPokemon.getPossibleEvolutions().size())
                .isBetween(0, 8);
        assertThat(capturedPokemon.getTypes().size())
                .isBetween(1, 2);
        assertThat(capturedPokemon.getPhotoUrl())
                .isNotEmpty();
    }

}
