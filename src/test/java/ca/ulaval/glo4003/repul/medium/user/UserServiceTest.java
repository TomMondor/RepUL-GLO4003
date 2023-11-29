package ca.ulaval.glo4003.repul.medium.user;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.account.AccountFactory;
import ca.ulaval.glo4003.repul.user.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.domain.account.Name;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenDecoder;
import ca.ulaval.glo4003.repul.user.infrastructure.InMemoryAccountRepository;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.CryptPasswordEncoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.InMemoryUserRepository;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.JWTTokenDecoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.JWTTokenGenerator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserServiceTest {
    private static final Email AN_EMAIL = new Email("test420@ulaval.ca");
    private static final Password A_PASSWORD = new Password("password");
    private static final IDUL AN_IDUL = new IDUL("TEST420");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(22));
    private static final Gender A_GENDER = Gender.MAN;
    private TokenDecoder tokenDecoder;
    private UserService userService;

    @BeforeEach
    public void createUserService() {
        tokenDecoder = new JWTTokenDecoder();
        userService =
            new UserService(new InMemoryAccountRepository(), new InMemoryUserRepository(), new AccountFactory(), new UserFactory(new CryptPasswordEncoder()),
                new UniqueIdentifierFactory(), new JWTTokenGenerator(), new CryptPasswordEncoder(), new GuavaEventBus());
    }

    @Test
    public void givenRegisteredUser_whenLoggingIn_shouldLogIn() {
        RegistrationQuery registrationQuery = new RegistrationQuery(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);
        userService.register(registrationQuery);

        assertDoesNotThrow(() -> userService.login(new LoginQuery(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void givenRegisteredUser_whenGettingAccount_shouldGetAccount() {
        RegistrationQuery registrationQuery = new RegistrationQuery(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);
        userService.register(registrationQuery);
        Token token = userService.login(new LoginQuery(AN_EMAIL, A_PASSWORD));
        UniqueIdentifier accountId = tokenDecoder.decode(token.value()).userId();

        assertDoesNotThrow(() -> userService.getAccount(accountId));
    }
}
