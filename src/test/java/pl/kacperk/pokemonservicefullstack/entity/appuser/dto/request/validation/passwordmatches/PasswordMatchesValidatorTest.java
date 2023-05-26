package pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.validation.passwordmatches;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_PASS;

class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator passMatchesValidator;

    @BeforeEach
    void setUp() {
        passMatchesValidator = new PasswordMatchesValidator();
    }

    @Test
    void isValid_passChangeRequestDtoMatchingPass_valid() {
        final var passChangeRequestDto = new AppUserPasswordChangeRequestDto(
            TEST_USER_PASS, TEST_USER_PASS
        );

        final var result = passMatchesValidator.isValid(passChangeRequestDto, null);

        assertThat(result)
            .isTrue();
    }

    @Test
    void isValid_passChangeRequestDtoNonMatchingPass_notValid() {
        final var passChangeRequestDto = new AppUserPasswordChangeRequestDto(
            TEST_USER_PASS, TEST_USER_NAME
        );

        final var result = passMatchesValidator.isValid(passChangeRequestDto, null);

        assertThat(result)
            .isFalse();
    }

    @Test
    void isValid_registerRequestDtoMatchingPass_valid() {
        final var registerRequestDto = new AppUserRegisterRequestDto();
        registerRequestDto.setPassword(TEST_USER_PASS);
        registerRequestDto.setMatchingPassword(TEST_USER_PASS);

        final var result = passMatchesValidator.isValid(registerRequestDto, null);

        assertThat(result)
            .isTrue();
    }

    @Test
    void isValid_registerRequestDtoNonMatchingPass_notValid() {
        final var registerRequestDto = new AppUserRegisterRequestDto();
        registerRequestDto.setPassword(TEST_USER_PASS);
        registerRequestDto.setMatchingPassword(TEST_USER_NAME);

        final var result = passMatchesValidator.isValid(registerRequestDto, null);

        assertThat(result)
            .isFalse();
    }

}
