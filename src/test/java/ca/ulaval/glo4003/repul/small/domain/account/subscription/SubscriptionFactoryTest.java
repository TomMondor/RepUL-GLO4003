package ca.ulaval.glo4003.repul.small.domain.account.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionFactoryTest {
    private static final LocalDate A_START_DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate A_END_DATE = LocalDate.of(2020, 1, 15);
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(new LocationId("VACHON"), "Doe", 10);
    private static final Lunchbox A_LUNCHBOX = new Lunchbox(List.of(new Recipe("recipe", 100, List.of(new Ingredient("ingredient", new Quantity(1, "kg"))))));
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final LunchboxType A_LUNCHBOX_TYPE = LunchboxType.STANDARD;
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = UniqueIdentifier.from(UUID.randomUUID().toString());

    @Mock
    private UniqueIdentifierFactory uniqueIdentifierFactory;
    private SubscriptionFactory subscriptionFactory;

    @BeforeEach
    public void setup() {
        this.subscriptionFactory = new SubscriptionFactory(uniqueIdentifierFactory);
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenCreateSubscription_shouldCreateSubscriptionWithParameters() {
        Subscription subscription = subscriptionFactory.createSubscription(A_START_DATE, A_END_DATE, A_PICKUP_LOCATION,
            A_LUNCHBOX, A_DAY_OF_WEEK, A_LUNCHBOX_TYPE);

        assertEquals(A_START_DATE, subscription.getStartDate());
        assertEquals(A_PICKUP_LOCATION, subscription.getPickupLocation());
        assertEquals(A_UNIQUE_IDENTIFIER, subscription.getSubscriptionId());
        assertEquals(A_DAY_OF_WEEK, subscription.getFrequency().dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE, subscription.getLunchboxType());
    }

    @Test
    public void whenCreateSubscription_shouldCreateSubscriptionWithCorrectAmountOfOrders() {
        int expectedOrderCount =  2;

        Subscription subscription = subscriptionFactory.createSubscription(A_START_DATE, A_END_DATE, A_PICKUP_LOCATION,
            A_LUNCHBOX, A_DAY_OF_WEEK, A_LUNCHBOX_TYPE);

        assertEquals(expectedOrderCount, subscription.getOrders().size());
    }
}
