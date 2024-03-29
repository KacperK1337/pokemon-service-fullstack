package pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.validation.passwordmatches.PasswordMatches;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class AppUserPasswordChangeRequestDto {

    @Size(min = 8, max = 60, message = "New password must be between 8 and 60 characters")
    private String password;
    private String matchingPassword;

}
