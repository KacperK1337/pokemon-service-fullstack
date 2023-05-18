package pl.kacperk.pokemonservicefullstack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.repo.PokemonRepo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DbDataLoaderTest {

    private static final int TOTAL_RESOURCE_TEST_USERS = 2;
    private static final int TOTAL_RESOURCE_TEST_POKEMONS = 905;

    @Mock
    private AppUserRepo userRepo;
    @Mock
    private PokemonRepo pokemonRepo;
    @Mock
    private PasswordEncoder passEncoder;
    private DbDataLoader dbDataLoader;

    @BeforeEach
    void setUp() {
        dbDataLoader = new DbDataLoader(userRepo, pokemonRepo, passEncoder);
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
        pokemonRepo.deleteAll();
    }

    @Test
    void start_usersResourcesNotEmpty_resourcesSavedToRepo() {
        dbDataLoader.start();

        verify(passEncoder, times(TOTAL_RESOURCE_TEST_USERS))
            .encode(any());
        verify(userRepo, times(TOTAL_RESOURCE_TEST_USERS))
            .save(any());
    }

    @Test
    void start_pokemonResourcesNotEmpty_resourcesSavedToRepo() {
        dbDataLoader.start();

        verify(pokemonRepo, times(TOTAL_RESOURCE_TEST_POKEMONS))
            .save(any());
    }

}
