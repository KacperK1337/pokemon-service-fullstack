package pl.kacperk.pokemonservicefullstack.util.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator passwordMatchesValidator;
    private AppUserPasswordChangeRequestDto passwordChangeRequestDto;
    private AppUserRegisterRequestDto registerRequestDto;

    @BeforeEach
    void setUp() {
        passwordMatchesValidator = new PasswordMatchesValidator();
    }

    @Test
    void isValid_instanceOfAppUserPasswordChangeRequestDtoAndSamePasswords_valid() {
        // given
        String password = "pass";
        passwordChangeRequestDto = new AppUserPasswordChangeRequestDto(password, password);

        // when
        boolean result = passwordMatchesValidator.isValid(passwordChangeRequestDto, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isValid_instanceOfAppUserPasswordChangeRequestDtoAndDifferentPasswords_notValid() {
        // given
        String password = "pass";
        passwordChangeRequestDto = new AppUserPasswordChangeRequestDto(password, password.toUpperCase());

        // when
        boolean result = passwordMatchesValidator.isValid(passwordChangeRequestDto, null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void isValid_instanceOfAppUserRegisterRequestDtoAndSamePasswords_valid() {
        // given
        String password = "pass";
        registerRequestDto = new AppUserRegisterRequestDto(password, password);

        // when
        boolean result = passwordMatchesValidator.isValid(registerRequestDto, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isValid_instanceOfAppUserRegisterRequestDtoAndDifferentPasswords_notValid() {
        // given
        String password = "pass";
        registerRequestDto = new AppUserRegisterRequestDto(password, password.toUpperCase());

        // when
        boolean result = passwordMatchesValidator.isValid(registerRequestDto, null);

        // then
        assertThat(result).isFalse();
    }
}