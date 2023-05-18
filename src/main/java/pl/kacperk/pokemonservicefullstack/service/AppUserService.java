package pl.kacperk.pokemonservicefullstack.service;

import jakarta.servlet.ServletException;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

public interface AppUserService {

    AppUser getAppUserByName(String userName);

    long getNumberOfUsers();

    AppUser getLoggedAppUser(AppUserDetails details);

    AppUserResponseDto getAppUserAsResponse(AppUser appUser);

    void registerAppUser(AppUserRegisterRequestDto registerRequestDto) throws UserAlreadyExistException;

    void changeLoggedUserPassword(AppUserDetails details,
                                  AppUserPasswordChangeRequestDto passwordChangeRequestDto) throws ServletException;
}
