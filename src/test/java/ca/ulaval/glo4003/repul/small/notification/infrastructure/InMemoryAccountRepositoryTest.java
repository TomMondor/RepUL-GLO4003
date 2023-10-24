package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryAccountRepositoryTest {
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final Account AN_ACCOUNT = new Account(AN_ACCOUNT_ID, AN_EMAIL);
    private InMemoryAccountRepository inMemoryAccountRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryAccountRepository = new InMemoryAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccount_shouldReturnAccount() {
        inMemoryAccountRepository.saveOrUpdate(AN_ACCOUNT);

        assertEquals(AN_ACCOUNT, inMemoryAccountRepository.getByAccountId(AN_ACCOUNT_ID).get());
    }

    @Test
    public void givenNoAccount_whenGettingAccount_shouldReturnOptionalOfEmpty() {
        assertTrue(inMemoryAccountRepository.getByAccountId(AN_ACCOUNT_ID).isEmpty());
    }
}
