package pl.kacperk.pokemonservicefullstack.api.appuser.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pl.kacperk.pokemonservicefullstack.TestUtils;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.dto.response.AppUserResponseDto;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.api.appuser.model.AppUserRole;
import pl.kacperk.pokemonservicefullstack.api.appuser.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetails;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsMapper;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest httpServletRequest;

    private AppUserServiceImpl underTest;
    private AppUser testAppUser;

    @BeforeEach
    void setUp() {
        underTest = new AppUserServiceImpl(appUserRepo, passwordEncoder, httpServletRequest);
        testAppUser = TestUtils.getTestAppUser();
    }

    @Test
    void getAppUser_existingId_findByIdMethodInvoked() {
        // given
        Long appUserId = testAppUser.getId();

        given(appUserRepo.findById(appUserId)).willReturn(Optional.of(testAppUser));

        // when
        underTest.getAppUserById(appUserId);

        // then
        verify(appUserRepo).findById(appUserId);
    }

    @Test
    void getAppUser_nonExistingId_throwResponseStatusException() {
        // given
        Long appUserId = testAppUser.getId();

        given(appUserRepo.findById(appUserId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getAppUserById(appUserId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining(String.format("User with id %s not found", appUserId));
    }

    @Test
    void getAppUser_existingUserName_findByUserNameMethodInvoked() {
        // given
        String appUserName = testAppUser.getUserName();

        given(appUserRepo.findByUserName(appUserName)).willReturn(Optional.of(testAppUser));

        // when
        underTest.getAppUserByName(appUserName);

        // then
        verify(appUserRepo).findByUserName(appUserName);
    }

    @Test
    void getAppUser_nonExistingUserName_throwResponseStatusException() {
        // given
        String appUserName = testAppUser.getUserName();

        given(appUserRepo.findByUserName(appUserName)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getAppUserByName(appUserName))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining(String.format("User with username %s not found", appUserName));
    }

    @Test
    void getNumberOfUsers_countByRoleMethodInvoked() {
        // when
        underTest.getNumberOfUsers();

        // then
        verify(appUserRepo).countByRole(AppUserRole.USER);
    }

    @Test
    void getLoggedAppUser_nullAppUserDetails_nullAppUser() {
        // when
        AppUser expectedAppUser = underTest.getLoggedAppUser(null);

        // then
        verify(appUserRepo, never()).findByUserName(any());

        assertThat(expectedAppUser).isNull();
    }

    @Test
    void getLoggedAppUser_notNullAppUserDetails_getAppUserMethodInvoked() {
        // given
        AppUserDetails testDetails = AppUserDetailsMapper.appUserToAppUserDetails(testAppUser);
        String detailsUsername = testDetails.getUsername();

        given(appUserRepo.findByUserName(detailsUsername)).willReturn(Optional.of(testAppUser));

        // when
        underTest.getLoggedAppUser(testDetails);

        // then
        verify(appUserRepo).findByUserName(detailsUsername);
    }

    @Test
    void getAppUserAsResponse_nullAppUser_nullResponse() {
        // when
        AppUserResponseDto expectedResponse = underTest.getAppUserAsResponse(null);

        // then
        assertThat(expectedResponse).isNull();
    }

    @Test
    void getAppUserAsResponse_notNullAppUser_notNullResponse() {
        // when
        AppUserResponseDto expectedResponse = underTest.getAppUserAsResponse(testAppUser);

        // then
        assertThat(expectedResponse).isNotNull();
    }

    @Test
    void registerAppUser_usernameNotTaken_userSavedToRepoAndEqual() throws UserAlreadyExistException {
        // given
        String appUserName = testAppUser.getUserName();
        String appUserPassword = testAppUser.getPassword();
        AppUserRegisterRequestDto testRegisterRequestDto =
                new AppUserRegisterRequestDto(appUserName, appUserPassword, appUserPassword);

        given(appUserRepo.findByUserName(appUserName)).willReturn(Optional.empty());
        given(passwordEncoder.encode(appUserPassword)).willReturn(appUserPassword.toUpperCase());

        // when
        underTest.registerAppUser(testRegisterRequestDto);

        // then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepo).save(appUserArgumentCaptor.capture());
        AppUser capturedAppUser = appUserArgumentCaptor.getValue();

        assertThat(capturedAppUser.getUserName()).isEqualTo(appUserName);
        assertThat(capturedAppUser.getPassword()).isEqualTo(appUserPassword.toUpperCase());
    }

    @Test
    void registerAppUser_usernameTaken_throwUserAlreadyExistException() {
        // given
        String appUserName = testAppUser.getUserName();
        String appUserPassword = testAppUser.getPassword();
        AppUserRegisterRequestDto testRegisterRequestDto =
                new AppUserRegisterRequestDto(appUserName, appUserPassword, appUserPassword);

        given(appUserRepo.findByUserName(any())).willReturn(Optional.of(testAppUser));

        // when
        // then
        assertThatThrownBy(() -> underTest.registerAppUser(testRegisterRequestDto))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("An account with that username already exists");

        verify(passwordEncoder, never()).encode(any());
        verify(appUserRepo, never()).save(any());
    }

    @Test
    void changeLoggedUserPassword_userLoggedIn_passwordChangedAndUserLoggedOut() throws ServletException {
        // given
        AppUserDetails testDetails = AppUserDetailsMapper.appUserToAppUserDetails(testAppUser);
        String appUserName = testDetails.getUsername();
        String appUserPassword = testDetails.getPassword();
        AppUserPasswordChangeRequestDto testPasswordChangeRequestDto =
                new AppUserPasswordChangeRequestDto(appUserPassword);

        given(appUserRepo.findByUserName(appUserName)).willReturn(Optional.of(testAppUser));
        given(passwordEncoder.encode(appUserPassword)).willReturn(appUserPassword.toUpperCase());

        // when
        underTest.changeLoggedUserPassword(testDetails, testPasswordChangeRequestDto);

        // then
        verify(httpServletRequest).logout();

        assertThat(testAppUser.getPassword()).isEqualTo(appUserPassword.toUpperCase());
    }

    @Test
    void changeLoggedUserPassword_userNotLoggedIn_throwResponseStatusException() throws ServletException {
        // given
        AppUserPasswordChangeRequestDto testPasswordChangeRequestDto = new AppUserPasswordChangeRequestDto();

        // when
        // then
        assertThatThrownBy(() -> underTest.changeLoggedUserPassword(null, testPasswordChangeRequestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED)
                .hasMessageContaining("User is not logged in");

        verify(appUserRepo, never()).findByUserName(any());
        verify(passwordEncoder, never()).encode(any());
        verify(httpServletRequest, never()).logout();
    }

}