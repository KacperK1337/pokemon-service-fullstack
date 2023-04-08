package pl.kacperk.pokemonservicefullstack.util.dbdataloader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DbDataLoaderUtilsTest {

    @Test
    void createResourceBufferedReader_existingResourcePath_success() throws IOException {
        // given
        final var resourcePath = "static/db/pokemons_data.txt";

        // when
        try (final var bufferedReader = DbDataLoaderUtils.createResourceBufferedReader(resourcePath)) {
            // then
            assertThat(bufferedReader)
                    .isNotNull();
            assertThat(bufferedReader.readLine())
                    .isNotNull();
        }
    }

    @Test
    void createResourceBufferedReader_nonExistingResourcePath_throwsException() {
        // given
        final var resourcePath = "nonExistingResourcePath";

        // when then
        assertThatThrownBy(() -> DbDataLoaderUtils.createResourceBufferedReader(resourcePath))
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "There is no resource with path " + resourcePath
                );
    }
}