package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryUserAccountRepositoryTest {
    private static final UniqueIdentifier AN_ACCOUNT_VALID_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_ACCOUNT_INVALID_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();

    @Mock
    private UserAccount userAccount;
    private InMemoryUserAccountRepository inMemoryUserAccountRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryUserAccountRepository = new InMemoryUserAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccountById_shouldReturnOptionalOfRightAccount() {
        given(userAccount.getAccountId()).willReturn(AN_ACCOUNT_VALID_ID);

        inMemoryUserAccountRepository.saveOrUpdate(userAccount);
        Optional<UserAccount> accountFound = inMemoryUserAccountRepository.getAccountById(
            AN_ACCOUNT_VALID_ID);

        assertEquals(Optional.of(userAccount), accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccountById_shouldReturnOptionalOfEmpty() {
        Optional<UserAccount> accountFound = inMemoryUserAccountRepository.getAccountById(
            AN_ACCOUNT_INVALID_ID);

        assertTrue(accountFound.isEmpty());
    }

    @Test
    public void whenSavingAccountAndGettingAccountByMealKitId_shouldReturnOptionalOfRightAccount() {
        given(userAccount.getAccountId()).willReturn(AN_ACCOUNT_VALID_ID);
        given(userAccount.getMealKitIds()).willReturn(List.of(A_MEAL_KIT_ID));

        inMemoryUserAccountRepository.saveOrUpdate(userAccount);
        Optional<UserAccount> accountFound = inMemoryUserAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID);

        assertEquals(Optional.of(userAccount), accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccountByMealKitId_shouldReturnOptionalOfEmpty() {
        Optional<UserAccount> accountFound = inMemoryUserAccountRepository.getAccountByMealKitId(
            AN_ACCOUNT_INVALID_ID);

        assertTrue(accountFound.isEmpty());
    }
}
