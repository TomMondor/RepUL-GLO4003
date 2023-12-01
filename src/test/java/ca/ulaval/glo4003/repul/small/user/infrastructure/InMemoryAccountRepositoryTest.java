package ca.ulaval.glo4003.repul.small.user.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.application.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.AccountRepository;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.infrastructure.InMemoryAccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryAccountRepositoryTest {

    private static final IDUL ACCOUNT_VALID_IDUL = new IDUL("ALMAT69");
    private static final SubscriberUniqueIdentifier ACCOUNT_VALID_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();

    @Mock
    private Account account;

    private AccountRepository inMemoryAccountRepository;

    @BeforeEach
    public void createAccountRepository() {
        inMemoryAccountRepository = new InMemoryAccountRepository();
    }

    @Test
    public void givenNoAccount_whenCheckingIfAccountExists_shouldReturnFalse() {
        boolean userExists = inMemoryAccountRepository.exists(ACCOUNT_VALID_IDUL);

        assertFalse(userExists);
    }

    @Test
    public void givenExistingAccount_whenCheckingIfAccountExists_shouldReturnTrue() {
        given(account.getIdul()).willReturn(ACCOUNT_VALID_IDUL);
        inMemoryAccountRepository.save(account);

        boolean userExists = inMemoryAccountRepository.exists(ACCOUNT_VALID_IDUL);

        assertTrue(userExists);
    }

    @Test
    public void whenSavingAccountAndGettingById_shouldReturnTheRightAccount() {
        given(account.getAccountId()).willReturn(ACCOUNT_VALID_ACCOUNT_ID);

        inMemoryAccountRepository.save(account);
        Account accountFound = inMemoryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID);

        assertEquals(account, accountFound);
    }

    @Test
    public void givenNonExistingAccount_whenGettingById_shouldThrowAccountNotFoundException() {
        assertThrows(AccountNotFoundException.class, () -> inMemoryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID));
    }
}
