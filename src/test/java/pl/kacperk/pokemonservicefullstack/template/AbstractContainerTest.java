package pl.kacperk.pokemonservicefullstack.template;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.kacperk.pokemonservicefullstack.container.PostgreSQLContainerRule;

@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractContainerTest {

    public static final PostgreSQLContainerRule CONTAINER = PostgreSQLContainerRule.getInstance();

    private static final String SPRING_DB_URL = "spring.datasource.url";
    private static final String SPRING_DB_USERNAME = "spring.datasource.username";
    private static final String SPRING_DB_PASS = "spring.datasource.password";

    static {
        CONTAINER.start();
    }

    @DynamicPropertySource
    static void databaseProperties(final DynamicPropertyRegistry registry) {
        registry.add(SPRING_DB_URL, CONTAINER::getJdbcUrl);
        registry.add(SPRING_DB_USERNAME, CONTAINER::getUsername);
        registry.add(SPRING_DB_PASS, CONTAINER::getPassword);
    }

}
