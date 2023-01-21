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

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final AppUserDetailsService appUserDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        //templates
                        "/", "/about", "/account/update-success", "/database", "/faqs",
                        "/pokemon", "/ranking", "/auth/register", "/auth/register-success",
                        //api
                        "/api/pokemons/get/**", "/api/users/register",
                        //static
                        "/css/**", "/img/**", "/js/**").permitAll()
                .requestMatchers("pokemon-favourite", "/account", "/account/update").hasRole("USER")
                .requestMatchers("/api/pokemons/like/*").hasAuthority("pokemons:like")
                .anyRequest().authenticated()

                .and()

                .formLogin()
                .loginPage("/auth/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login-error")
                .passwordParameter("password")
                .usernameParameter("username")

                .and()

                .rememberMe()
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                .key("e7XaTsNq#QBR!Xa")
                .userDetailsService(appUserDetailsService)
                .rememberMeParameter("remember-me")

                .and()

                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/")

                .and().build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

}