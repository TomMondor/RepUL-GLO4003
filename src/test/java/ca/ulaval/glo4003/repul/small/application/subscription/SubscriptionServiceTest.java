package ca.ulaval.glo4003.repul.small.application.subscription;

import java.time.DayOfWeek;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final String A_LOCATION_STRING = "VACHON";
    private static final String A_DAY_STRING = "MONDAY";
    private static final String A_LUNCHBOX_TYPE = "STANDARD";
    private static final SubscriptionQuery SUBSCRIPTION_QUERY = new SubscriptionQuery(A_LOCATION_STRING, A_DAY_STRING, A_LUNCHBOX_TYPE);
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Amount AN_AMOUNT = new Amount(15.2);
    private SubscriptionService subscriptionService;

    @Mock
    private PaymentHandler paymentHandler;
    @Mock
    private RepULRepository repULRepository;
    @Mock
    private RepUL repUL;

    @BeforeEach
    public void createSubscriptionService() {
        when(repULRepository.get()).thenReturn(repUL);
        subscriptionService = new SubscriptionService(repULRepository, paymentHandler);
    }

    @Test
    public void whenCreatingSubscription_shouldGetRepUL() {
        subscriptionService.createSubscription(AN_ACCOUNT_ID, SUBSCRIPTION_QUERY);

        verify(repULRepository).get();
    }

    @Test
    public void whenCreatingSubscription_shouldCreateSubscription() {
        subscriptionService.createSubscription(AN_ACCOUNT_ID, SUBSCRIPTION_QUERY);

        verify(repUL).createSubscription(any(UniqueIdentifier.class), eq(new LocationId(A_LOCATION_STRING)), eq(DayOfWeek.valueOf(A_DAY_STRING)), eq(
            LunchboxType.valueOf(A_LUNCHBOX_TYPE)));
    }

    @Test
    public void whenCreatingSubscription_shouldSaveOrUpdateRepUL() {
        subscriptionService.createSubscription(AN_ACCOUNT_ID, SUBSCRIPTION_QUERY);

        verify(repULRepository).saveOrUpdate(repUL);
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        when(repUL.createSubscription(any(UniqueIdentifier.class), any(LocationId.class),
            any(DayOfWeek.class), any(LunchboxType.class))).thenReturn(A_SUBSCRIPTION_ID);

        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, SUBSCRIPTION_QUERY);

        assertEquals(A_SUBSCRIPTION_ID, subscriptionId);
    }

    @Test
    public void whenGettingSubscriptions_shouldGetRepUL() {
        subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        verify(repULRepository).get();
    }

    @Test
    public void whenGettingSubscriptions_shouldGetSubscriptions() {
        subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        verify(repUL).getSubscriptions(AN_ACCOUNT_ID);
    }

    @Test
    public void whenConfirmingNextLunchboxForSubscription_shouldGetRepUL() {
        subscriptionService.confirmNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repULRepository).get();
    }

    @Test
    public void whenConfirmingNextLunchboxForSubscription_shouldConfirmNextLunchboxForSubscription() {
        subscriptionService.confirmNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repUL).confirmNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenConfirmingNextLunchboxForSubscription_shouldGetLunchboxPrice() {
        subscriptionService.confirmNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repUL).getLunchboxPrice(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenConfirmingNextLunchboxForSubscription_shouldMakeTransactionWithLunchBoxPrice() {
        when(repUL.getLunchboxPrice(any(UniqueIdentifier.class), any(UniqueIdentifier.class))).thenReturn(AN_AMOUNT);

        subscriptionService.confirmNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(paymentHandler).makeTransaction(AN_AMOUNT);
    }

    @Test
    public void whenConfirmingNextLunchboxForSubscription_shouldSaveOrUpdateRepUL() {
        subscriptionService.confirmNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repULRepository).saveOrUpdate(repUL);
    }

    @Test
    public void whenDecliningNextLunchboxForSubscription_shouldGetRepUL() {
        subscriptionService.declineNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repULRepository).get();
    }

    @Test
    public void whenDecliningNextLunchboxForSubscription_shouldDeclineNextLunchboxForSubscription() {
        subscriptionService.declineNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repUL).declineNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenDecliningNextLunchboxForSubscription_shouldSaveOrUpdateRepUL() {
        subscriptionService.declineNextLunchboxForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(repULRepository).saveOrUpdate(repUL);
    }
}
