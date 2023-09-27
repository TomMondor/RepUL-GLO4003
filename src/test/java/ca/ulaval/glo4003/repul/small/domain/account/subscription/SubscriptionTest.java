package ca.ulaval.glo4003.repul.small.domain.account.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionTest {
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Frequency FREQUENCY = new Frequency(DayOfWeek.FRIDAY);
    private static final PickupLocation PICKUP_LOCATION = new PickupLocation(new LocationId("123"), "123", 10);
    private static final LocalDate START_DATE = LocalDate.now().plusDays(3);
    private static final LunchboxType LUNCHBOX_TYPE = LunchboxType.STANDARD;
    @Mock
    private Order order;

    @Test
    public void givenSubscriptionWithOrderInTheFuture_whenConfirm_shouldConfirmNextOrder() {
        given(order.isInTheFuture()).willReturn(true);
        List<Order> orders = Arrays.asList(order, order);
        Subscription subscription = new Subscription(A_SUBSCRIPTION_ID, orders, FREQUENCY, PICKUP_LOCATION, START_DATE, LUNCHBOX_TYPE);

        subscription.confirmNextLunchbox();

        verify(order).confirm();
    }

    @Test
    public void givenSubscriptionWithOrderInTheFuture_whenDecline_shouldDeclineNextOrder() {
        given(order.isInTheFuture()).willReturn(true);
        List<Order> orders = Arrays.asList(order, order);
        Subscription subscription = new Subscription(A_SUBSCRIPTION_ID, orders, FREQUENCY, PICKUP_LOCATION, START_DATE, LUNCHBOX_TYPE);

        subscription.declineNextLunchbox();

        verify(order).decline();
    }
}
