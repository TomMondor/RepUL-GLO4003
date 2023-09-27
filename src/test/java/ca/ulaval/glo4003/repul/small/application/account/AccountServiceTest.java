package ca.ulaval.glo4003.repul.small.application.account;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.application.AuthFacade;
import ca.ulaval.glo4003.repul.application.account.AccountService;
import ca.ulaval.glo4003.repul.application.account.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.AccountFactory;
import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.account.Gender;
import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.account.Name;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private static final String AN_IDUL = "ALMAT69";
    private static final String AN_EMAIL = "anEmail@ulaval.ca";
    private static final String A_PASSWORD = "a@*nfF8KA1";
    private static final String A_NAME = "aName";
    private static final String A_BIRTHDATE = "2000-01-01";
    private static final String A_GENDER = "MAN";
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());

    private AccountService accountService;

    @Mock
    private RepULRepository repULRepository;

    @Mock
    private AccountFactory accountFactory;

    @Mock
    private AuthFacade authFacade;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(repULRepository, accountFactory, authFacade);
    }

    @Test
    public void whenRegistering_shouldRetrieveRepUL() {
        given(repULRepository.get()).willReturn(mock(RepUL.class));

        accountService.register(RegistrationQuery.from(AN_IDUL, AN_EMAIL, A_PASSWORD, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(repULRepository).get();
    }

    @Test
    public void whenRegistering_shouldCallAuthService() {
        given(repULRepository.get()).willReturn(mock(RepUL.class));

        accountService.register(RegistrationQuery.from(AN_IDUL, AN_EMAIL, A_PASSWORD, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(authFacade).register(AN_EMAIL, A_PASSWORD);
    }

    @Test
    public void whenRegistering_shouldCreateAccount() {
        given(repULRepository.get()).willReturn(mock(RepUL.class));
        given(authFacade.register(any(), any())).willReturn(AN_ACCOUNT_ID);
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(mock(Account.class));

        accountService.register(RegistrationQuery.from(AN_IDUL, AN_EMAIL, A_PASSWORD, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(accountFactory).createAccount(AN_ACCOUNT_ID, new Name(A_NAME), new Birthdate(LocalDate.parse(A_BIRTHDATE)), Gender.MAN, new IDUL(AN_IDUL),
            new Email(AN_EMAIL));
    }

    @Test
    public void whenRegistering_shouldAddAccountToRepUL() {
        RepUL repUL = mock(RepUL.class);
        Account mockAccount = mock(Account.class);
        given(repULRepository.get()).willReturn(repUL);
        given(authFacade.register(any(), any())).willReturn(AN_ACCOUNT_ID);
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(mockAccount);

        accountService.register(RegistrationQuery.from(AN_IDUL, AN_EMAIL, A_PASSWORD, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(repUL).addAccount(mockAccount);
    }

    @Test
    public void whenRegistering_shouldSaveUpdatedRepUL() {
        RepUL repUL = mock(RepUL.class);
        given(repULRepository.get()).willReturn(repUL);
        given(authFacade.register(any(), any())).willReturn(AN_ACCOUNT_ID);
        given(accountFactory.createAccount(any(), any(), any(), any(), any(), any())).willReturn(mock(Account.class));

        accountService.register(RegistrationQuery.from(AN_IDUL, AN_EMAIL, A_PASSWORD, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(repULRepository).saveOrUpdate(repUL);
    }
}
