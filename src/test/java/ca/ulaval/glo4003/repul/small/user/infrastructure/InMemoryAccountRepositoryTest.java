package ca.ulaval.glo4003.repul.small.user.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.AccountRepository;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.infrastructure.InMemoryAccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryAccountRepositoryTest {

    private static final IDUL ACCOUNT_VALID_IDUL = new IDUL("ALMAT69");
    private static final UniqueIdentifier ACCOUNT_VALID_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());

    @Mock
    private Account account;

    private AccountRepository accountRepository;

    @BeforeEach
    public void createAccountRepository() {
        accountRepository = new InMemoryAccountRepository();
    }

    @Test
    public void givenValidAccount_whenSaveOrUpdate_shouldSaveInRepository() {
        given(account.idul()).willReturn(ACCOUNT_VALID_IDUL);

        accountRepository.saveOrUpdate(account);

        assertTrue(accountRepository.getByIDUL(ACCOUNT_VALID_IDUL).isPresent());
    }

    @Test
    public void givenExistingAccount_whenGetByIDUL_shouldReturnRightAccount() {
        given(account.idul()).willReturn(ACCOUNT_VALID_IDUL);
        accountRepository.saveOrUpdate(account);

        Optional<Account> accountFound = accountRepository.getByIDUL(ACCOUNT_VALID_IDUL);

        assertEquals(Optional.of(account), accountFound);
    }

    @Test
    public void givenNonExistingAccount_whenGetByIDUL_shouldReturnEmptyOptional() {
        Optional<Account> accountFound = accountRepository.getByIDUL(ACCOUNT_VALID_IDUL);

        assertTrue(accountFound.isEmpty());
    }

    @Test
    public void givenExistingAccount_whenGetById_shouldReturnRightAccount() {
        given(account.accountId()).willReturn(ACCOUNT_VALID_ACCOUNT_ID);
        accountRepository.saveOrUpdate(account);

        Optional<Account> accountFound = accountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID);

        assertEquals(Optional.of(account), accountFound);
    }

    @Test
    public void givenNonExistingAccount_whenGetById_shouldReturnEmptyOptional() {
        Optional<Account> accountFound = accountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID);

        assertTrue(accountFound.isEmpty());
    }
}
