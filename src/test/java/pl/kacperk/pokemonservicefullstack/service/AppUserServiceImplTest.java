package pl.kacperk.pokemonservicefullstack.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kacperk.pokemonservicefullstack.AbstractMockitoTest;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserPasswordChangeRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.dto.request.AppUserRegisterRequestDto;
import pl.kacperk.pokemonservicefullstack.entity.appuser.model.AppUser;
import pl.kacperk.pokemonservicefullstack.repo.AppUserRepo;
import pl.kacperk.pokemonservicefullstack.security.userdetails.AppUserDetailsMapper;
import pl.kacperk.pokemonservicefullstack.util.exception.UserAlreadyExistException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ServiceUtils.NOT_FOUND_STATUS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ServiceUtils.RESPONSE_STATUS_EXC_CLASS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ServiceUtils.STATUS_PROP;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ServiceUtils.UNAUTHORIZED_STATUS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.ServiceUtils.USER_NOT_LOGGED_MESS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.ROLE_USER;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_ID;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_NAME;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.TEST_USER_PASS;
import static pl.kacperk.pokemonservicefullstack.TestUtils.UserUtils.createTestAppUserWithId;

class AppUserServiceImplTest extends AbstractMockitoTest {

    private static final Class<UserAlreadyExistException> USER_ALREADY_EXIST_EXC_CLASS =
        UserAlreadyExistException.class;
    private static final String USER_NOT_FOUND_BY_ID_MESS = "User with id %s not found";
    private static final String USER_NOT_FOUND_BY_NAME_MESS = "User with username %s not found";
    private static final String USER_ALREADY_EXIST_MESS = "An account with that username already exists";
    private static final String TEST_USER_ENCODED_PASS = "testUserEncodedPass";

    @Mock
    private AppUserRepo userRepo;
    @Mock
    private PasswordEncoder passEncoder;
    @Mock
    private HttpServletRequest httpServletRequest;
    private AppUserServiceImpl userServiceImpl;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        userServiceImpl = new AppUserServiceImpl(
            userRepo, passEncoder, httpServletRequest
        );
        testUser = createTestAppUserWithId();
    }

    @Test
    void getAppUser_existingId_findByIdMethodInvoked() {
        given(userRepo.findById(TEST_USER_ID))
            .willReturn(Optional.of(testUser));

        userServiceImpl.getAppUserById(TEST_USER_ID);

        verify(userRepo)
            .findById(TEST_USER_ID);
    }

    @Test
    void getAppUser_nonExistingId_throwResponseStatusException() {
        given(userRepo.findById(TEST_USER_ID))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.getAppUserById(TEST_USER_ID))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, NOT_FOUND_STATUS)
            .hasMessageContaining(
                String.format(USER_NOT_FOUND_BY_ID_MESS, TEST_USER_ID)
            );
    }

    @Test
    void getAppUser_existingUserName_findByUserNameMethodInvoked() {
        given(userRepo.findByUserName(TEST_USER_NAME))
            .willReturn(Optional.of(testUser));

        userServiceImpl.getAppUserByName(TEST_USER_NAME);

        verify(userRepo)
            .findByUserName(TEST_USER_NAME);
    }

    @Test
    void getAppUser_nonExistingUserName_throwResponseStatusException() {
        given(userRepo.findByUserName(TEST_USER_NAME))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.getAppUserByName(TEST_USER_NAME))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, NOT_FOUND_STATUS)
            .hasMessageContaining(
                String.format(USER_NOT_FOUND_BY_NAME_MESS, TEST_USER_NAME)
            );
    }

    @Test
    void getNumberOfUsers_countByRoleMethodInvoked() {
        userServiceImpl.getNumberOfUsers();

        verify(userRepo)
            .countByRole(ROLE_USER);
    }

    @Test
    void getLoggedAppUser_nullAppUserDetails_nullAppUser() {
        final var loggedAppUser = userServiceImpl.getLoggedAppUser(null);

        verify(userRepo, never())
            .findByUserName(any());
        assertThat(loggedAppUser)
            .isNull();
    }

    @Test
    void getLoggedAppUser_notNullAppUserDetails_getAppUserMethodInvoked() {
        final var testDetails = AppUserDetailsMapper.appUserToAppUserDetails(testUser);
        final var testDetailsUsername = testDetails.getUsername();
        given(userRepo.findByUserName(testDetailsUsername))
            .willReturn(Optional.of(testUser));

        userServiceImpl.getLoggedAppUser(testDetails);

        verify(userRepo)
            .findByUserName(testDetailsUsername);
    }

    @Test
    void getAppUserAsResponse_nullAppUser_nullResponse() {
        final var userResponse = userServiceImpl.getAppUserAsResponse(null);

        assertThat(userResponse)
            .isNull();
    }

    @Test
    void getAppUserAsResponse_notNullAppUser_notNullResponse() {
        final var userResponse = userServiceImpl.getAppUserAsResponse(testUser);

        assertThat(userResponse)
            .isNotNull();
    }

    @Test
    void registerAppUser_usernameNotTaken_userSavedToRepoAndEqual() throws UserAlreadyExistException {
        final var testRegisterRequestDto = new AppUserRegisterRequestDto(
            TEST_USER_NAME, TEST_USER_PASS, TEST_USER_PASS
        );
        given(userRepo.findByUserName(TEST_USER_NAME))
            .willReturn(Optional.empty());
        given(passEncoder.encode(TEST_USER_PASS))
            .willReturn(TEST_USER_ENCODED_PASS);

        userServiceImpl.registerAppUser(testRegisterRequestDto);

        final var userArgumentCaptor = forClass(AppUser.class);
        verify(userRepo)
            .save(userArgumentCaptor.capture());
        final var capturedAppUser = userArgumentCaptor.getValue();
        assertThat(capturedAppUser.getUserName())
            .isEqualTo(TEST_USER_NAME);
        assertThat(capturedAppUser.getPassword())
            .isEqualTo(TEST_USER_ENCODED_PASS);
    }

    @Test
    void registerAppUser_usernameTaken_throwUserAlreadyExistException() {
        final var testRegisterRequestDto = new AppUserRegisterRequestDto(
            TEST_USER_NAME, TEST_USER_PASS, TEST_USER_PASS
        );

        given(userRepo.findByUserName(any()))
            .willReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userServiceImpl.registerAppUser(testRegisterRequestDto))
            .isInstanceOf(USER_ALREADY_EXIST_EXC_CLASS)
            .hasMessageContaining(USER_ALREADY_EXIST_MESS);
        verify(passEncoder, never())
            .encode(any());
        verify(userRepo, never())
            .save(any());
    }

    @Test
    void changeLoggedUserPass_userLoggedIn_passChangedUserLoggedOut() throws ServletException {
        final var testDetails = AppUserDetailsMapper.appUserToAppUserDetails(testUser);
        final var testUserName = testDetails.getUsername();
        final var testUserPass = testDetails.getPassword();
        final var testPassChangeRequestDto = new AppUserPasswordChangeRequestDto(testUserPass);
        given(userRepo.findByUserName(testUserName))
            .willReturn(Optional.of(testUser));
        given(passEncoder.encode(testUserPass))
            .willReturn(TEST_USER_ENCODED_PASS);

        userServiceImpl.changeLoggedUserPassword(testDetails, testPassChangeRequestDto);

        verify(httpServletRequest)
            .logout();
        assertThat(testUser.getPassword())
            .isEqualTo(TEST_USER_ENCODED_PASS);
    }

    @Test
    void changeLoggedUserPass_userNotLoggedIn_throwResponseStatusException() throws ServletException {
        final var testPassChangeRequestDto = new AppUserPasswordChangeRequestDto();

        assertThatThrownBy(() -> userServiceImpl.changeLoggedUserPassword(null, testPassChangeRequestDto))
            .isInstanceOf(RESPONSE_STATUS_EXC_CLASS)
            .hasFieldOrPropertyWithValue(STATUS_PROP, UNAUTHORIZED_STATUS)
            .hasMessageContaining(USER_NOT_LOGGED_MESS);
        verify(userRepo, never())
            .findByUserName(any());
        verify(passEncoder, never())
            .encode(any());
        verify(httpServletRequest, never())
            .logout();
    }

}
