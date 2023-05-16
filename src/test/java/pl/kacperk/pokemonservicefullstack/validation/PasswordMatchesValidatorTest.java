package pl.kacperk.pokemonservicefullstack.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_PASS;

class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator passwordMatchesValidator;

    @BeforeEach
    void setUp() {
        passwordMatchesValidator = new PasswordMatchesValidator();
    }

    @Test
    void isValid_passwordChangeRequestDtoMatchingPass_valid() {
        final var passwordChangeRequestDto = new AppUserPasswordChangeRequestDto(
            TEST_USER_PASS, TEST_USER_PASS
        );

        final var result = passwordMatchesValidator.isValid(passwordChangeRequestDto, null);

        assertThat(result)
            .isTrue();
    }

    @Test
    void isValid_passwordChangeRequestDtoNonMatchingPass_notValid() {
        final var passwordChangeRequestDto = new AppUserPasswordChangeRequestDto(
            TEST_USER_PASS, TEST_USER_NAME
        );

        final var result = passwordMatchesValidator.isValid(passwordChangeRequestDto, null);

        assertThat(result)
            .isFalse();
    }

    @Test
    void isValid_registerRequestDtoMatchingPass_valid() {
        final var registerRequestDto = new AppUserRegisterRequestDto(
            TEST_USER_PASS, TEST_USER_PASS
        );

        final var result = passwordMatchesValidator.isValid(registerRequestDto, null);

        assertThat(result)
            .isTrue();
    }

    @Test
    void isValid_registerRequestDtoNonMatchingPass_notValid() {
        final var registerRequestDto = new AppUserRegisterRequestDto(
            TEST_USER_PASS, TEST_USER_NAME
        );

        final var result = passwordMatchesValidator.isValid(registerRequestDto, null);

        assertThat(result)
            .isFalse();
    }

}
