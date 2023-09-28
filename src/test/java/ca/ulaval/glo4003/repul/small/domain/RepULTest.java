package ca.ulaval.glo4003.repul.small.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.fixture.AccountFixture;
import ca.ulaval.glo4003.repul.fixture.RepULFixture;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepULTest {
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());

    @Test
    public void givenInexistantAccount_whenFindAccountById_shouldThrowAccountNotFoundException() {
        RepUL repUL = new RepULFixture().build();

        assertThrows(AccountNotFoundException.class, () -> {
            repUL.findAccountById(AN_INVALID_ACCOUNT_ID);
        });
    }

    @Test
    public void givenExistantAccount_whenFindAccountById_shouldReturnAccount() {
        RepUL repUL = new RepULFixture().addAccount(new AccountFixture().withAccountId(AN_ACCOUNT_ID).build()).build();

        repUL.findAccountById(AN_ACCOUNT_ID);
    }
}
