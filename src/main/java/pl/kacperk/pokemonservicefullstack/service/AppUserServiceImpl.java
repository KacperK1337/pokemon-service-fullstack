package pl.kacperk.pokemonservicefullstack.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.kacperk.pokemonservicefullstack.entity.appuser.dto.response.AppUserResponseDtoMapper.appUserToAppUserResponseDto;
import static pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUserRole.USER;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    protected static final String USER_NOT_FOUND_BY_NAME_MESS = "User with username %s not found";
    protected static final String USER_ALREADY_EXIST_MESS = "An account with that username already exists";
    protected static final String USER_NOT_LOGGED_MESS = "User is not logged in";

    private final AppUserRepo userRepo;
    private final PasswordEncoder passEncoder;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AppUser getUserByName(final String userName) {
        return userRepo
            .findByUserName(userName)
            .orElseThrow(() ->
                new ResponseStatusException(
                    NOT_FOUND, String.format(USER_NOT_FOUND_BY_NAME_MESS, userName)
                ));
    }

    @Override
    public long getNumberOfUsers() {
        return userRepo.countByRole(USER);
    }

    @Override
    public AppUser getLoggedUser(final AppUserDetails userDetails) {
        if (userDetails != null) {
            final String userName = userDetails.getUsername();
            return getUserByName(userName);
        } else {
            return null;
        }
    }

    @Override
    public AppUserResponseDto getUserAsResponse(AppUser user) {
        return (user == null ? null : appUserToAppUserResponseDto(user));
    }

    @Transactional
    @Override
    public void registerUser(final AppUserRegisterRequestDto registerRequestDto) throws UserAlreadyExistException {
        final String requestUserName = registerRequestDto.getUserName();
        if (
            userRepo
                .findByUserName(requestUserName)
                .isPresent()
        ) {
            throw new UserAlreadyExistException(USER_ALREADY_EXIST_MESS);
        }
        final String encodedRequestPass = passEncoder.encode(
            registerRequestDto.getPassword()
        );
        final AppUser requestUser = new AppUser(
            USER, requestUserName, encodedRequestPass
        );
        userRepo.save(requestUser);
    }

    @Transactional
    @Override
    public void changeLoggedUserPassword(
        final AppUserDetails userDetails,
        final AppUserPasswordChangeRequestDto passChangeRequestDto
    ) throws ServletException {
        final AppUser loggedUser = getLoggedUser(userDetails);
        if (loggedUser == null) {
            throw new ResponseStatusException(
                UNAUTHORIZED, USER_NOT_LOGGED_MESS
            );
        }
        final String newEncodedPass = passEncoder.encode(
            passChangeRequestDto.getPassword()
        );
        loggedUser.setPassword(newEncodedPass);
        httpServletRequest.logout();
    }

}
