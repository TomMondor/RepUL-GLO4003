package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliveryPersonAccountRepositoryTest {
    private static final UniqueIdentifier ACCOUNT_VALID_ID = new UniqueIdentifierFactory().generate();
    @Mock
    private DeliveryPersonAccount deliveryPersonAccount;
    private InMemoryDeliveryPersonAccountRepository inMemoryDeliveryPersonAccountRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryDeliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();
    }

    @Test
    public void whenSavingAccountAndGettingAccount_shouldReturnOptionalOfRightAccount() {
        given(deliveryPersonAccount.getAccountId()).willReturn(ACCOUNT_VALID_ID);

        inMemoryDeliveryPersonAccountRepository.saveOrUpdate(deliveryPersonAccount);
        Optional<DeliveryPersonAccount> accountFound = inMemoryDeliveryPersonAccountRepository.getByAccountId(ACCOUNT_VALID_ID);

        assertEquals(Optional.of(deliveryPersonAccount), accountFound);
    }

    @Test
    public void givenNoAccount_whenGettingAccount_shouldReturnOptionalOfEmpty() {
        Optional<DeliveryPersonAccount> accountFound = inMemoryDeliveryPersonAccountRepository.getByAccountId(ACCOUNT_VALID_ID);

        assertTrue(accountFound.isEmpty());
    }
}
