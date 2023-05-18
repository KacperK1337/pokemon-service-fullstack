package pl.kacperk.pokemonservicefullstack.service;

import jakarta.servlet.ServletException;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

public interface AppUserService {

    AppUser getUserByName(final String userName);

    long getNumberOfUsers();

    AppUser getLoggedUser(final AppUserDetails userDetails);

    AppUserResponseDto getUserAsResponse(final AppUser user);

    void registerUser(final AppUserRegisterRequestDto registerRequestDto) throws UserAlreadyExistException;

    void changeLoggedUserPassword(
        final AppUserDetails details, final AppUserPasswordChangeRequestDto passChangeRequestDto
    ) throws ServletException;
}
