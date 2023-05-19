package pl.kacperk.pokemonservicefullstack.template;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.kacperk.pokemonservicefullstack.container.PostgreSQLContainerRule;

@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractContainerTest {

    public static final PostgreSQLContainerRule CONTAINER = PostgreSQLContainerRule.getInstance();

    static {
        CONTAINER.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", CONTAINER::getUsername);
        registry.add("spring.datasource.password", CONTAINER::getPassword);
    }

}
