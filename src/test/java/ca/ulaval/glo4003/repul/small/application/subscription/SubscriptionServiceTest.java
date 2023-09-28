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
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final String A_LOCATION_STRING = "VACHON";
    private static final String A_DAY_STRING = "MONDAY";
    private static final SubscriptionQuery SUBSCRIPTION_QUERY = new SubscriptionQuery(A_LOCATION_STRING, A_DAY_STRING);
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private SubscriptionService subscriptionService;

    @Mock
    private PaymentHandler paymentHandler;
    @Mock
    private RepULRepository repULRepository;
    @Mock
    private RepUL mockRepUL;

    @BeforeEach
    public void createSubscriptionService() {
        when(repULRepository.get()).thenReturn(mockRepUL);
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

        verify(mockRepUL).createSubscription(any(UniqueIdentifier.class), eq(new LocationId(A_LOCATION_STRING)), eq(DayOfWeek.valueOf(A_DAY_STRING)));
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        when(mockRepUL.createSubscription(any(UniqueIdentifier.class), any(LocationId.class), any(DayOfWeek.class))).thenReturn(A_SUBSCRIPTION_ID);

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

        verify(mockRepUL).getSubscriptions(AN_ACCOUNT_ID);
    }
}
