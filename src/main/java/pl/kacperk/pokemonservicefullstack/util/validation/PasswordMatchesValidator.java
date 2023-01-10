package pl.kacperk.pokemonservicefullstack.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;


public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        String password = "";
        String matching = "";
        if (obj instanceof AppUserPasswordChangeRequestDto passwordChangeRequestDto) {
            password = passwordChangeRequestDto.getPassword();
            matching = passwordChangeRequestDto.getMatchingPassword();
        } else if (obj instanceof AppUserRegisterRequestDto registerRequestDto) {
            password = registerRequestDto.getPassword();
            matching = registerRequestDto.getMatchingPassword();
        }
        return password.equals(matching);
    }
}
