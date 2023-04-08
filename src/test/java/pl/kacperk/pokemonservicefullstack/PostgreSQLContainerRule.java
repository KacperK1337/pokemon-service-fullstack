package pl.kacperk.pokemonservicefullstack;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLContainerRule extends PostgreSQLContainer<PostgreSQLContainerRule> {

    private static final String IMAGE_VERSION = "postgres:15.2";

    private static volatile PostgreSQLContainerRule INSTANCE;

    public static PostgreSQLContainerRule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PostgreSQLContainerRule();
        }
        return INSTANCE;
    }

    public PostgreSQLContainerRule() {
        super(IMAGE_VERSION);
    }

}
