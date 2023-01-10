package pl.kacperk.pokemonservicefullstack.api.appuser.service;

import jakarta.servlet.ServletException;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

public interface AppUserService {

    AppUser getAppUserById(Long id);

    AppUser getAppUserByName(String userName);

    long getNumberOfUsers();

    AppUser getLoggedAppUser(AppUserDetails details);

    AppUserResponseDto getAppUserAsResponse(AppUser appUser);

    void registerAppUser(AppUserRegisterRequestDto registerRequestDto) throws UserAlreadyExistException;

    void changeLoggedUserPassword(AppUserDetails details,
                                  AppUserPasswordChangeRequestDto passwordChangeRequestDto) throws ServletException;

    void deleteLoggedUser(AppUserDetails details) throws ServletException;
}
