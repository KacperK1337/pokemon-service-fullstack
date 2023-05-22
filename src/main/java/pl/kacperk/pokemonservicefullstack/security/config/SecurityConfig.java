package pl.kacperk.pokemonservicefullstack.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsService;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private static final String INDEX_PATTERN = "/";
    private static final String ABOUT_PATTERN = "/about";
    private static final String ACCOUNT_PATTERN = "/account";
    private static final String ACCOUNT_UPDATE_PATTERN = "/account/update";
    private static final String ACCOUNT_UPDATE_SUCCESS_PATTERN = "/account/update-success";
    private static final String DATABASE_PATTERN = "/database";
    private static final String FAQS_PATTERN = "/faqs";
    private static final String POKEMON_PATTERN = "/pokemon";
    private static final String POKEMON_FAVOURITE_PATTERN = "/pokemon-favourite";
    private static final String RANKING_PATTERN = "/ranking";

    private static final String AUTH_REGISTER_PATTERN = "/auth/register";
    private static final String AUTH_REGISTER_SUCCESS_PATTERN = "/auth/register-success";
    private static final String AUTH_LOGIN_PATTERN = "/auth/login";
    private static final String AUTH_LOGIN_ERROR_PATTERN = "/auth/login-error";
    private static final String LOGOUT_PATTERN = "/logout";

    private static final String POKEMONS_GET_PATTERN = "/api/pokemons/get/**";
    private static final String POKEMONS_LIKE_PATTERN = "/api/pokemons/like/*";
    private static final String USERS_REGISTER_PATTERN = "/api/users/register";

    private static final String STATIC_PATTERN = "/static/**";
    private static final String STATIC_CSS_PATTERN = "/css/**";
    private static final String STATIC_IMG_PATTERN = "/img/**";
    private static final String STATIC_JS_PATTERN = "/js/**";

    private static final String PASS_PARAM = "password";
    private static final String USERNAME_PARAM = "username";

    private static final String REMEMBER_ME_KEY = randomAscii(16);
    private static final String REMEMBER_ME_PARAM = "remember-me";
    private static final String JSESSION_ID_PARAM = "JSESSIONID";
    private static final int REMEMBER_ME_VALIDITY_TIME = (int) DAYS.toSeconds(21);

    private static final String USER_ROLE = "USER";
    private static final String POKEMONS_LIKE_PERMISSION = "pokemons:like";

    private final PasswordEncoder passwordEncoder;
    private final AppUserDetailsService userDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
            .ignoring()
            .requestMatchers(STATIC_PATTERN);
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(
                //templates
                INDEX_PATTERN, ABOUT_PATTERN, ACCOUNT_UPDATE_SUCCESS_PATTERN, DATABASE_PATTERN, FAQS_PATTERN,
                POKEMON_PATTERN, RANKING_PATTERN, AUTH_REGISTER_PATTERN, AUTH_REGISTER_SUCCESS_PATTERN,
                //api
                POKEMONS_GET_PATTERN, USERS_REGISTER_PATTERN,
                //static
                STATIC_CSS_PATTERN, STATIC_IMG_PATTERN, STATIC_JS_PATTERN).permitAll()
            .requestMatchers(
                POKEMON_FAVOURITE_PATTERN, ACCOUNT_PATTERN, ACCOUNT_UPDATE_PATTERN).hasRole(USER_ROLE)
            .requestMatchers(
                POKEMONS_LIKE_PATTERN).hasAuthority(POKEMONS_LIKE_PERMISSION)
            .anyRequest().authenticated()

            .and()

            .formLogin()
            .loginPage(AUTH_LOGIN_PATTERN)
            .permitAll()
            .defaultSuccessUrl(INDEX_PATTERN, true)
            .failureUrl(AUTH_LOGIN_ERROR_PATTERN)
            .passwordParameter(PASS_PARAM)
            .usernameParameter(USERNAME_PARAM)

            .and()

            .rememberMe()
            .tokenValiditySeconds(REMEMBER_ME_VALIDITY_TIME)
            .key(REMEMBER_ME_KEY)
            .userDetailsService(userDetailsService)
            .rememberMeParameter(REMEMBER_ME_PARAM)

            .and()

            .logout()
            .logoutUrl(LOGOUT_PATTERN)
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .deleteCookies(JSESSION_ID_PARAM, REMEMBER_ME_PARAM)
            .logoutSuccessUrl(INDEX_PATTERN)

            .and().build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

}
