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
import ca.ulaval.glo4003.identitymanagement.application.query.RegistrationQuery;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenGenerator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
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
    private TokenGenerator tokenGenerator;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(userRepository, userFactory, uniqueIdentifierFactory, tokenGenerator);
    }

    @Test
    public void givenExistingUser_whenRegistering_shouldThrowUserAlreadyExistsException() {
        given(userRepository.exists(AN_EMAIL)).willReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(new RegistrationQuery(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void givenUserNotYetRegistered_whenRegistering_shouldSaveUser() {
        User userToSave = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD);
        given(userRepository.exists(AN_EMAIL)).willReturn(false);
        given(uniqueIdentifierFactory.generate()).willReturn(A_UID);
        given(userFactory.createUser(A_UID, AN_EMAIL, A_PASSWORD)).willReturn(userToSave);

        authService.register(new RegistrationQuery(AN_EMAIL, A_PASSWORD));

        verify(userRepository).saveOrUpdate(userToSave);
    }

    @Test
    public void givenUserNotYetRegistered_whenRegistering_shouldGenerateToken() {
        User userToSave = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD);
        given(userRepository.exists(AN_EMAIL)).willReturn(false);
        given(uniqueIdentifierFactory.generate()).willReturn(A_UID);
        given(userFactory.createUser(A_UID, AN_EMAIL, A_PASSWORD)).willReturn(userToSave);

        authService.register(new RegistrationQuery(AN_EMAIL, A_PASSWORD));

        verify(tokenGenerator).generate(A_UID);
    }
}
