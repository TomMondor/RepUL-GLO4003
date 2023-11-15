package ca.ulaval.glo4003.repul.small.user.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.AccountRepository;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.infrastructure.InMemoryAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliveryPersonAccountRepositoryTest {

    private static final IDUL ACCOUNT_VALID_IDUL = new IDUL("ALMAT69");
    private static final UniqueIdentifier ACCOUNT_VALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();

    @Mock
    private Account account;

    private AccountRepository inMemoryAccountRepository;

    @BeforeEach
    public void createAccountRepository() {
        inMemoryAccountRepository = new InMemoryAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccountByIdul_shouldReturnOptionalOfRightAccount() {
        given(account.getIdul()).willReturn(ACCOUNT_VALID_IDUL);

        inMemoryAccountRepository.saveOrUpdate(account);
        Optional<Account> accountFound = inMemoryAccountRepository.getByIDUL(ACCOUNT_VALID_IDUL);

        assertEquals(Optional.of(account), accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccountByIDUL_shouldReturnOptionalOfEmpty() {
        Optional<Account> accountFound = inMemoryAccountRepository.getByIDUL(ACCOUNT_VALID_IDUL);

        assertTrue(accountFound.isEmpty());
    }

    @Test
    public void whenSavingAccountAndGettingById_shouldReturnOptionalOfRightAccount() {
        given(account.getAccountId()).willReturn(ACCOUNT_VALID_ACCOUNT_ID);

        inMemoryAccountRepository.saveOrUpdate(account);
        Optional<Account> accountFound = inMemoryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID);

        assertEquals(Optional.of(account), accountFound);
    }

    @Test
    public void givenNonExistingAccount_whenGettingById_shouldReturnEmptyOptional() {
        Optional<Account> accountFound = inMemoryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID);

        assertTrue(accountFound.isEmpty());
    }
}
