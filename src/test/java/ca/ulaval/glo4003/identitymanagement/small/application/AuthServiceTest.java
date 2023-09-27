package ca.ulaval.glo4003.identitymanagement.small.application;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.application.query.LoginQuery;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenGenerator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifier(UUID.randomUUID());
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Password A_PASSWORD = new Password("a@*nfF8KA1");
    private static final Password AN_ENCRYPTED_PASSWORD = new Password("encryptedPassword");

    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UniqueIdentifierFactory uniqueIdentifierFactory;

    @Mock
    private UserFactory userFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGenerator tokenGenerator;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(userRepository, userFactory, uniqueIdentifierFactory, tokenGenerator);
    }

    @Test
    public void givenExistingUser_whenRegistering_shouldThrowUserAlreadyExistsException() {
        given(userRepository.exists(AN_EMAIL)).willReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(AN_EMAIL.value(), A_PASSWORD.value()));
    }

    @Test
    public void givenUserNotYetRegistered_whenRegistering_shouldSaveUser() {
        User userToSave = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD, passwordEncoder);
        given(userRepository.exists(AN_EMAIL)).willReturn(false);
        given(uniqueIdentifierFactory.generate()).willReturn(A_UID);
        given(userFactory.createUser(A_UID, AN_EMAIL, A_PASSWORD)).willReturn(userToSave);

        authService.register(AN_EMAIL.value(), A_PASSWORD.value());

        verify(userRepository).saveOrUpdate(userToSave);
    }

    @Test
    public void whenLogin_shouldGetUser() {
        User user = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD, passwordEncoder);
        given(passwordEncoder.matches(A_PASSWORD, AN_ENCRYPTED_PASSWORD)).willReturn(true);
        given(userRepository.findByEmail(AN_EMAIL)).willReturn(user);

        authService.login(LoginQuery.from(AN_EMAIL.value(), A_PASSWORD.value()));

        verify(userRepository).findByEmail(AN_EMAIL);
    }

    @Test
    public void givenUserNotFound_whenLogin_shouldThrowInvalidCredentialsException() {
        given(userRepository.findByEmail(AN_EMAIL)).willReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(LoginQuery.from(AN_EMAIL.value(), A_PASSWORD.value())));
    }

    @Test
    public void whenLogin_shouldCheckPassword() {
        User mockUser = mock(User.class);
        given(userRepository.findByEmail(AN_EMAIL)).willReturn(mockUser);

        authService.login(LoginQuery.from(AN_EMAIL.value(), A_PASSWORD.value()));

        verify(mockUser).checkPasswordMatches(A_PASSWORD);
    }

    @Test
    public void whenLogin_shouldGenerateToken() {
        User user = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD, passwordEncoder);
        given(passwordEncoder.matches(A_PASSWORD, AN_ENCRYPTED_PASSWORD)).willReturn(true);
        given(userRepository.findByEmail(AN_EMAIL)).willReturn(user);

        authService.login(LoginQuery.from(AN_EMAIL.value(), A_PASSWORD.value()));

        verify(tokenGenerator).generate(A_UID);
    }
}
