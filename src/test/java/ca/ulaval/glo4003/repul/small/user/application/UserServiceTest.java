package ca.ulaval.glo4003.repul.small.user.application;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.user.AccountFixture;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.user.application.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.AccountFactory;
import ca.ulaval.glo4003.repul.user.domain.account.AccountRepository;
import ca.ulaval.glo4003.repul.user.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.domain.account.Name;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final String AN_IDUL = "ALMAT69";
    private static final String AN_EMAIL = "john@ulaval.ca";
    private static final String A_PASSWORD = "a@*nfF8KA1";
    private static final Password AN_ENCRYPTED_PASSWORD = new Password("encryptedPassword");
    private static final String A_NAME = "aName";
    private static final String A_BIRTHDATE = "2000-01-01";
    private static final String A_GENDER = "MAN";
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());

    private UserService userService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountFactory accountFactory;

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
        userService = new UserService(accountRepository, userRepository, accountFactory, userFactory, uniqueIdentifierFactory, tokenGenerator, repULEventBus);
    }

    @Test
    public void givenRegistrationQueryWithExistingEmail_whenRegistering_shouldThrowUserAlreadyExistsException() {
        given(userRepository.exists(any())).willReturn(true);

        assertThrows(UserAlreadyExistsException.class,
            () -> userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER)));
    }

    @Test
    public void givenRegistrationQueryWithExistingEmail_whenRegistering_shouldNotSaveNewUser() {
        given(userRepository.exists(any())).willReturn(true);

        try {
            userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));
        } catch (UserAlreadyExistsException ignored) {
            // Ignoring the exception because we want to check the repositories
        }

        verify(userRepository, never()).saveOrUpdate(any());
        verify(accountRepository, never()).saveOrUpdate(any());
    }

    @Test
    public void givenRegistrationQueryWithExistingIDUL_whenRegistering_shouldThrowUserAlreadyExistsException() {
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.of(mock(Account.class)));

        assertThrows(UserAlreadyExistsException.class,
            () -> userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER)));
    }

    @Test
    public void givenRegistrationQueryWithExistingIDUL_whenRegistering_shouldNotSaveNewUser() {
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.of(mock(Account.class)));

        try {
            userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));
        } catch (UserAlreadyExistsException ignored) {
            // Ignoring the exception because we want to check the repositories
        }

        verify(userRepository, never()).saveOrUpdate(any());
        verify(accountRepository, never()).saveOrUpdate(any());
    }

    @Test
    public void whenRegistering_shouldCreateUser() {
        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier(UUID.randomUUID());
        given(uniqueIdentifierFactory.generate()).willReturn(uniqueIdentifier);
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.empty());
        given(userFactory.createUser(any(), any(), any(), any())).willReturn(mock(User.class));
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(mock(Account.class));
        doNothing().when(repULEventBus).publish(any(AccountCreatedEvent.class));

        userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(userFactory).createUser(uniqueIdentifier, new Email(AN_EMAIL), Role.CLIENT, new Password(A_PASSWORD));
    }

    @Test
    public void whenRegistering_shouldSaveUser() {
        User newUser = mock(User.class);
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.empty());
        given(userFactory.createUser(any(), any(), any(), any())).willReturn(newUser);
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(mock(Account.class));
        doNothing().when(repULEventBus).publish(any(AccountCreatedEvent.class));

        userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(userRepository).saveOrUpdate(newUser);
    }

    @Test
    public void whenRegistering_shouldCreateAccount() {
        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier(UUID.randomUUID());
        given(uniqueIdentifierFactory.generate()).willReturn(uniqueIdentifier);
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.empty());
        given(userFactory.createUser(any(), any(), any(), any())).willReturn(mock(User.class));
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(mock(Account.class));

        userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(accountFactory).createAccount(uniqueIdentifier, new IDUL(AN_IDUL), new Name(A_NAME), new Birthdate(LocalDate.parse(A_BIRTHDATE)),
            Gender.from(A_GENDER), new Email(AN_EMAIL));
    }

    @Test
    public void whenRegistering_shouldSaveAccount() {
        Account newAccount = mock(Account.class);
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.empty());
        given(userFactory.createUser(any(), any(), any(), any())).willReturn(mock(User.class));
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(newAccount);

        userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(accountRepository).saveOrUpdate(newAccount);
    }

    @Test
    public void whenRegistering_shouldPublishAccountCreatedEvent() {
        Account newAccount = mock(Account.class);
        given(userRepository.exists(any())).willReturn(false);
        given(accountRepository.getByIDUL(any())).willReturn(Optional.empty());
        given(userFactory.createUser(any(), any(), any(), any())).willReturn(mock(User.class));
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(newAccount);

        userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(repULEventBus).publish(any());
    }

    @Test
    public void whenLogin_shouldFindUserByEmail() {
        User user = new User(AN_ACCOUNT_ID, new Email(AN_EMAIL), AN_ENCRYPTED_PASSWORD, Role.CLIENT, passwordEncoder);
        given(passwordEncoder.matches(new Password(A_PASSWORD), AN_ENCRYPTED_PASSWORD)).willReturn(true);
        given(userRepository.findByEmail(new Email(AN_EMAIL))).willReturn(user);

        userService.login(LoginQuery.from(AN_EMAIL, A_PASSWORD));

        verify(userRepository).findByEmail(new Email(AN_EMAIL));
    }

    @Test
    public void givenUserNotFound_whenLogin_shouldThrowInvalidCredentialsException() {
        given(userRepository.findByEmail(new Email(AN_EMAIL))).willReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(LoginQuery.from(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void whenLogin_shouldCheckPassword() {
        User mockUser = mock(User.class);
        given(userRepository.findByEmail(new Email(AN_EMAIL))).willReturn(mockUser);

        userService.login(LoginQuery.from(AN_EMAIL, A_PASSWORD));

        verify(mockUser).checkPasswordMatches(new Password(A_PASSWORD));
    }

    @Test
    public void whenLogin_shouldGenerateToken() {
        User user = new User(AN_ACCOUNT_ID, new Email(AN_EMAIL), AN_ENCRYPTED_PASSWORD, Role.CLIENT, passwordEncoder);
        given(passwordEncoder.matches(new Password(A_PASSWORD), AN_ENCRYPTED_PASSWORD)).willReturn(true);
        given(userRepository.findByEmail(new Email(AN_EMAIL))).willReturn(user);

        userService.login(LoginQuery.from(AN_EMAIL, A_PASSWORD));

        verify(tokenGenerator).generate(AN_ACCOUNT_ID, Role.CLIENT);
    }

    @Test
    public void whenGettingAccount_shouldFindAccountById() {
        given(accountRepository.getByAccountId(any())).willReturn(Optional.of(new AccountFixture().build()));

        userService.getAccount(AN_ACCOUNT_ID);

        verify(accountRepository).getByAccountId(AN_ACCOUNT_ID);
    }

    @Test
    public void givenInexistentAccount_whenGettingAccount_shouldThrowAccountNotFoundException() {
        given(accountRepository.getByAccountId(any())).willReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> userService.getAccount(AN_ACCOUNT_ID));
    }

    @Test
    public void givenExistingAccount_whenGettingAccount_shouldReturnAccountInformationPayload() {
        Account account = new AccountFixture().build();
        given(accountRepository.getByAccountId(any())).willReturn(Optional.of(account));

        AccountInformationPayload payload = userService.getAccount(AN_ACCOUNT_ID);

        assertEquals(account.idul(), payload.idul());
        assertEquals(account.name(), payload.name());
        assertEquals(account.birthdate(), payload.birthdate());
        assertEquals(account.gender(), payload.gender());
        assertEquals(account.email(), payload.email());
    }
}
