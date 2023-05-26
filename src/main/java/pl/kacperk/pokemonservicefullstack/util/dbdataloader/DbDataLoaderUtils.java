package pl.kacperk.pokemonservicefullstack.util.dbdataloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;

public class DbDataLoaderUtils {

    protected static final String NO_RESOURCE_MESS = "There is no resource with path %s";

    public static BufferedReader createResourceBufferedReader(String resourcePath) {
        final ClassLoader classloader = currentThread().getContextClassLoader();
        final InputStream inputStream = classloader.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new NullPointerException(
                String.format(NO_RESOURCE_MESS, resourcePath)
            );
        }
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
        return new BufferedReader(inputStreamReader);
    }

}
