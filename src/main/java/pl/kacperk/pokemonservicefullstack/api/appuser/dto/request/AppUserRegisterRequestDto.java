package pl.kacperk.pokemonservicefullstack.api.appuser.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kacperk.pokemonservicefullstack.util.validation.PasswordMatches;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class AppUserRegisterRequestDto {

    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    private String userName;

    @Size(min = 8, max = 60, message = "Password must be between 8 and 60 characters")
    private String password;

    private String matchingPassword;

    public AppUserRegisterRequestDto(String password, String matchingPassword) {
        this.password = password;
        this.matchingPassword = matchingPassword;
    }
}
