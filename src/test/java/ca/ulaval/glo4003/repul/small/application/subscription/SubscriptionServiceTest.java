package ca.ulaval.glo4003.repul.small.application.subscription;

import java.time.DayOfWeek;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final String A_LOCATION_STRING = "VACHON";
    private static final String A_DAY_STRING = "MONDAY";
    private static final SubscriptionParams SUBSCRIPTION_PARAMS = new SubscriptionParams(A_LOCATION_STRING, A_DAY_STRING);
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
        subscriptionService.createSubscription(SUBSCRIPTION_PARAMS);

        verify(repULRepository).get();
    }

    @Test
    public void whenCreatingSubscription_shouldCreateSubscription() {
        subscriptionService.createSubscription(SUBSCRIPTION_PARAMS);

        verify(mockRepUL).createSubscription(any(UniqueIdentifier.class), eq(new LocationId(A_LOCATION_STRING)), eq(DayOfWeek.valueOf(A_DAY_STRING)));
    }
}
