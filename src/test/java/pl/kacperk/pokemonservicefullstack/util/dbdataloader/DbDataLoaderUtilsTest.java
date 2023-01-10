package pl.kacperk.pokemonservicefullstack.util.dbdataloader;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DbDataLoaderUtilsTest {

    @Test
    void createResourceBufferedReader_existingResourcePath_success() throws IOException {
        // given
        String resourcePath = "static/db/pokemons_data.txt";

        // when
        BufferedReader bufferedReader = DbDataLoaderUtils.createResourceBufferedReader(resourcePath);

        // then
        assertThat(bufferedReader).isNotNull();
        assertThat(bufferedReader.readLine()).isNotNull();
    }

    @Test
    void createResourceBufferedReader_nonExistingResourcePath_throwsException() {
        // given
        String resourcePath = "nonExistingResourcePath";

        // when then
        assertThatThrownBy(() -> DbDataLoaderUtils.createResourceBufferedReader(resourcePath))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("There is no resource with path " + resourcePath);
    }
}