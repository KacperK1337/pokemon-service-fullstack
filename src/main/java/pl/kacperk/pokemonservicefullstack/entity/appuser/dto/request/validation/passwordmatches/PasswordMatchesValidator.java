package pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.validation.passwordmatches;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        if (obj instanceof final AppUserPasswordChangeRequestDto passChangeRequestDto) {
            final String pass = passChangeRequestDto.getPassword();
            final String matchingPass = passChangeRequestDto.getMatchingPassword();

            return pass.equals(matchingPass);
        } else if (obj instanceof final AppUserRegisterRequestDto registerRequestDto) {
            final String pass = registerRequestDto.getPassword();
            final String matchingPass = registerRequestDto.getMatchingPassword();

            return pass.equals(matchingPass);
        } else {
            return false;
        }
    }

}
