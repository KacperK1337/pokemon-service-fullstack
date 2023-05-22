package pl.kacperk.pokemonservicefullstack.util.dbdataloader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pl.kacperk.pokemonservicefullstack.util.dbdataloader.DbDataLoaderUtils.NO_RESOURCE_MESS;
import static pl.kacperk.pokemonservicefullstack.util.dbdataloader.DbDataLoaderUtils.createResourceBufferedReader;

class DbDataLoaderUtilsTest {

    private static final String EXISTING_RESOURCE_PATH = "static/db/pokemons_data.txt";
    private static final String NON_EXISTING_RESOURCE_PATH = "nonExistingResourcePath";
    private static final Class<NullPointerException> NULL_POINTER_EXCEPTION_CLASS =
        NullPointerException.class;

    @Test
    void createResourceBufferedReader_existingResourcePath_success() throws IOException {
        final var bufferedReader = createResourceBufferedReader(EXISTING_RESOURCE_PATH);

        assertThat(bufferedReader)
            .isNotNull();
        assertThat(bufferedReader.readLine())
            .isNotNull();
        bufferedReader.close();
    }

    @Test
    void createResourceBufferedReader_nonExistingResourcePath_throwsException() {
        assertThatThrownBy(() -> createResourceBufferedReader(NON_EXISTING_RESOURCE_PATH))
            .isInstanceOf(NULL_POINTER_EXCEPTION_CLASS)
            .hasMessageContaining(
                String.format(NO_RESOURCE_MESS, NON_EXISTING_RESOURCE_PATH)
            );
    }

}