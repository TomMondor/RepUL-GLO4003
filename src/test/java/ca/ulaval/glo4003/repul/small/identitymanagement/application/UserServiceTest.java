package ca.ulaval.glo4003.repul.small.identitymanagement.application;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.identitymanagement.application.UserService;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Password;
import ca.ulaval.glo4003.repul.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.repul.identitymanagement.domain.User;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.TokenGenerator;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Email AN_EMAIL = new Email("john@ulaval.ca");
    private static final Password A_PASSWORD = new Password("a@*nfF8KA1");
    private static final Name A_NAME = new Name("aName");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.parse("2000-01-01"));
    private static final Gender A_GENDER = Gender.MAN;

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UniqueIdentifierFactory uniqueIdentifierFactory;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private RepULEventBus repULEventBus;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, userFactory, uniqueIdentifierFactory, tokenGenerator, passwordEncoder, repULEventBus);
    }

    @Test
    public void givenRegistrationQueryWithExistingEmail_whenRegistering_shouldThrowEmailAlreadyInUseException() {
        given(userRepository.exists(any(Email.class))).willReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.register(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));
    }

    @Test
    public void givenRegistrationQueryWithExistingIDUL_whenRegistering_shouldThrowIDULAlreadyInUseException() {
        given(userRepository.exists(any(Email.class))).willReturn(false);
        given(userRepository.exists(any(IDUL.class))).willReturn(true);

        assertThrows(IDULAlreadyInUseException.class, () -> userService.register(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));
    }

    @Test
    public void givenUserNotFound_whenLogin_shouldThrowInvalidCredentialsException() {
        given(userRepository.findByEmail(AN_EMAIL)).willReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(AN_EMAIL, A_PASSWORD));
    }

    @Test
    public void whenRegistering_shouldPublishUserCreatedEvent() {
        given(userRepository.exists(any(Email.class))).willReturn(false);
        given(userRepository.exists(any(IDUL.class))).willReturn(false);
        given(userFactory.createUser(any(), any(), any(), any(), any())).willReturn(mock(User.class));

        userService.register(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);

        verify(repULEventBus).publish(any(UserCreatedEvent.class));
    }
}
