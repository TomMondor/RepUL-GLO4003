package ca.ulaval.glo4003.repul.small.shipping.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryAccount;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryAccountRepository;
import ca.ulaval.glo4003.repul.shipping.infrastructure.InMemoryDeliveryAccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliveryAccountRepositoryTest {
    private static final UniqueIdentifier ACCOUNT_VALID_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());

    @Mock
    private DeliveryAccount deliveryAccount;
    private DeliveryAccountRepository deliveryAccountRepository;

    @BeforeEach
    public void createDeliveryAccountRepository() {
        deliveryAccountRepository = new InMemoryDeliveryAccountRepository();
    }

    @Test
    public void givenValidDeliveryAccount_whenSaveOrUpdate_shouldSaveInRepository() {
        given(deliveryAccount.getAccountId()).willReturn(ACCOUNT_VALID_ACCOUNT_ID);

        deliveryAccountRepository.saveOrUpdate(deliveryAccount);

        assertEquals(deliveryAccount, deliveryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID).get());
    }

    @Test
    public void givenExistingDeliveryAccount_whenGetByAccountId_shouldReturnRightDeliveryAccount() {
        given(deliveryAccount.getAccountId()).willReturn(ACCOUNT_VALID_ACCOUNT_ID);
        deliveryAccountRepository.saveOrUpdate(deliveryAccount);

        assertEquals(deliveryAccount, deliveryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID).get());
    }

    @Test
    public void givenNonExistingDeliveryAccount_whenGetByAccountId_shouldReturnEmptyOptional() {
        Optional<DeliveryAccount> accountFound = deliveryAccountRepository.getByAccountId(ACCOUNT_VALID_ACCOUNT_ID);

        assertTrue(accountFound.isEmpty());
    }

    @Test
    public void givenExistingDeliveryAccount_whenExists_shouldReturnTrue() {
        given(deliveryAccount.getAccountId()).willReturn(ACCOUNT_VALID_ACCOUNT_ID);
        deliveryAccountRepository.saveOrUpdate(deliveryAccount);

        assertTrue(deliveryAccountRepository.exists(ACCOUNT_VALID_ACCOUNT_ID));
    }

    @Test
    public void givenNonExistingDeliveryAccount_whenExists_shouldReturnFalse() {
        assertFalse(deliveryAccountRepository.exists(ACCOUNT_VALID_ACCOUNT_ID));
    }
}
