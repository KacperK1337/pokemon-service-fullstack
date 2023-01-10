package pl.kacperk.pokemonservicefullstack.api.appuser.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.mapper.AppUserResponseDtoMapper;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AppUser getAppUserById(Long id) {
        return appUserRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("User with id %s not found", id)
                ));
    }

    @Override
    public AppUser getAppUserByName(String userName) {
        return appUserRepo.findByUserName(userName).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("User with username %s not found", userName)
                ));
    }

    @Override
    public long getNumberOfUsers() {
        return appUserRepo.countByRole(AppUserRole.USER);
    }

    @Override
    public AppUser getLoggedAppUser(AppUserDetails details) {
        if (details != null) {
            String userName = details.getUsername();
            return getAppUserByName(userName);
        } else {
            return null;
        }
    }

    @Override
    public AppUserResponseDto getAppUserAsResponse(AppUser user) {
        return (user == null ? null : AppUserResponseDtoMapper.appUserToAppUserResponseDto(user));
    }

    @Transactional
    @Override
    public void registerAppUser(AppUserRegisterRequestDto registerRequestDto) throws UserAlreadyExistException {
        String requestedUserName = registerRequestDto.getUserName();
        if (appUserRepo.findByUserName(requestedUserName).isPresent()) {
            throw new UserAlreadyExistException("An account with that username already exists");
        }
        AppUser newUser = new AppUser(
                AppUserRole.valueOf("USER"),
                requestedUserName,
                passwordEncoder.encode(registerRequestDto.getPassword())
        );
        appUserRepo.save(newUser);
    }

    @Transactional
    @Override
    public void changeLoggedUserPassword(
            AppUserDetails details,
            AppUserPasswordChangeRequestDto passwordChangeRequestDto) throws ServletException {
        AppUser loggedUser = getLoggedAppUser(details);
        if (loggedUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User is not logged in"
            );
        }
        String newPassword = passwordChangeRequestDto.getPassword();
        loggedUser.setPassword(passwordEncoder.encode(newPassword));
        httpServletRequest.logout();
    }

    @Transactional
    @Override
    public void deleteLoggedUser(AppUserDetails details) throws ServletException {
        AppUser loggedUser = getLoggedAppUser(details);
        if (loggedUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User is not logged in"
            );
        }
        httpServletRequest.logout();
        appUserRepo.delete(loggedUser);
    }

}
