package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryAccountRepositoryTest {
    private static final UniqueIdentifier ACCOUNT_VALID_ID = new UniqueIdentifierFactory().generate();
    @Mock
    private Account account;
    private InMemoryAccountRepository inMemoryAccountRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryAccountRepository = new InMemoryAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccount_shouldReturnOptionalOfRightAccount() {
        given(account.accountId()).willReturn(ACCOUNT_VALID_ID);

        inMemoryAccountRepository.saveOrUpdate(account);
        Optional<Account> accountFound = inMemoryAccountRepository.getByAccountId(ACCOUNT_VALID_ID);

        assertEquals(Optional.of(account), accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccount_shouldReturnOptionalOfEmpty() {
        Optional<Account> accountFound = inMemoryAccountRepository.getByAccountId(ACCOUNT_VALID_ID);

        assertTrue(accountFound.isEmpty());
    }
}
