package ca.ulaval.glo4003.repul.small.application.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ca.ulaval.glo4003.commons.Email;
import ca.ulaval.glo4003.repul.application.auth.AuthService;
import ca.ulaval.glo4003.repul.application.auth.UserRepository;
import ca.ulaval.glo4003.repul.application.auth.parameter.LoginParams;
import ca.ulaval.glo4003.repul.application.auth.parameter.RegisterParams;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.repul.domain.exception.UserNotFoundException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    private static final Email A_VALID_EMAIL = new Email("Steve123@gmail.lo");
    private static final String A_VALID_PASSWORD = "a@*nfF8KA1";
    private static final String HASHED_PASSWORD = new BCryptPasswordEncoder().encode(A_VALID_PASSWORD);
    private static final String AN_INVALID_PASSWORD = new BCryptPasswordEncoder().encode("a");
    private static final Email AN_INVALID_EMAIL = new Email("NotAUser@a.lo");
    private static final LoginParams VALID_LOGIN_PARAMS = new LoginParams(A_VALID_EMAIL, A_VALID_PASSWORD);
    private static final LoginParams INVALID_LOGIN_PARAMS = new LoginParams(AN_INVALID_EMAIL, AN_INVALID_PASSWORD);
    private static final RegisterParams VALID_REGISTER_PARAMS = new RegisterParams(A_VALID_EMAIL, A_VALID_PASSWORD);
    private static final RegisterParams INVALID_REGISTER_PARAMS = new RegisterParams(AN_INVALID_EMAIL, AN_INVALID_PASSWORD);
    private static final String JWT_SECRET = "a";
    private UserRepository userRepository;
    private RepULRepository repULRepository;
    private RepUL repUL;
    private AuthService authService;

    @BeforeEach
    public void setup() {
        this.userRepository = mock(UserRepository.class);
        this.repULRepository = mock(RepULRepository.class);
        this.repUL = mock(RepUL.class);
        when(this.repULRepository.get()).thenReturn(this.repUL);
        this.authService = new AuthService(this.userRepository, this.repULRepository, new BCryptPasswordEncoder());
    }

    @Test
    public void givenAValidUser_whenLoginUser_ShouldReturnTokenWithEmail() {
        when(this.userRepository.getUserPassword(A_VALID_EMAIL)).thenReturn(HASHED_PASSWORD);

        String token = this.authService.login(VALID_LOGIN_PARAMS);

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET)).withClaim("email", A_VALID_EMAIL.value()).build();
        verifier.verify(token);
    }

    @Test
    public void givenAnInvalidUser_whenLoginUser_shouldThrowUserNotFoundException() {
        when(this.userRepository.getUserPassword(AN_INVALID_EMAIL)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> this.authService.login(INVALID_LOGIN_PARAMS));
    }

    @Test
    public void givenAUserNotYetRegister_whenRegisteringUser_shouldReturnToken() {
        when(this.userRepository.doesUserExist(A_VALID_EMAIL)).thenReturn(false);
        when(this.repULRepository.get()).thenReturn(mock(RepUL.class));

        String token = this.authService.register(VALID_REGISTER_PARAMS);

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET)).withClaim("email", A_VALID_EMAIL.value()).build();
        verifier.verify(token);
    }

    @Test
    public void givenAUserNotYetRegister_whenRegisteringUser_shouldAddUserToRepo() {
        when(this.userRepository.doesUserExist(A_VALID_EMAIL)).thenReturn(false);
        when(this.repULRepository.get()).thenReturn(mock(RepUL.class));

        this.authService.register(VALID_REGISTER_PARAMS);

        verify(this.userRepository, times(1)).addUser(eq(A_VALID_EMAIL), anyString());
    }

    @Test
    public void givenAnExistingUser_whenRegisteringUser_shouldThrowUserAlreadyExistException() {
        when(this.userRepository.doesUserExist(AN_INVALID_EMAIL)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> this.authService.register(INVALID_REGISTER_PARAMS));
    }

    @Test
    public void whenRegisteringUser_shouldCallRepUL() {
        when(this.userRepository.doesUserExist(A_VALID_EMAIL)).thenReturn(false);

        this.authService.register(VALID_REGISTER_PARAMS);

        verify(this.repUL, times(1)).register();
    }

    @Test
    public void whenRegisteringUser_shouldSaveRepULState() {
        when(this.userRepository.doesUserExist(A_VALID_EMAIL)).thenReturn(false);

        this.authService.register(VALID_REGISTER_PARAMS);
        verify(this.repULRepository, times(1)).saveOrUpdate(this.repUL);
    }
}
