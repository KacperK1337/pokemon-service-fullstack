package pl.kacperk.pokemonservicefullstack.util.dbdataloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DbDataLoaderUtils {

    protected static final String NO_RESOURCE_MESS = "There is no resource with path %s";

    public static BufferedReader createResourceBufferedReader(String resourcePath) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new NullPointerException(
                String.format(NO_RESOURCE_MESS, resourcePath)
            );
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }
}
