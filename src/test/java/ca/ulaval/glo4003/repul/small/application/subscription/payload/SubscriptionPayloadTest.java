package ca.ulaval.glo4003.repul.small.application.subscription.payload;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationPayload;
import ca.ulaval.glo4003.repul.application.order.payload.OrderPayload;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.SubscriptionFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionPayloadTest {
    private static final UniqueIdentifier SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Lunchbox ORDER_LUNCHBOX = new LunchboxFixture().build();
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;
    private static final OrderPayload ORDER_PAYLOAD = new OrderPayload(ORDER_LUNCHBOX, ORDER_DELIVERY_DATE, ORDER_STATUS);
    private static final OrdersPayload ORDERS_PAYLOAD = new OrdersPayload(List.of(ORDER_PAYLOAD));
    private static final Order ORDER =
        new OrderFixture().withLunchbox(ORDER_LUNCHBOX).withDeliveryDate(ORDER_DELIVERY_DATE).withOrderStatus(ORDER_STATUS).build();

    private static final Frequency SUBSCRIPTION_FREQUENCY = new Frequency(DayOfWeek.MONDAY);
    private static final LocationId LOCATION_ID = new LocationId("a location id");
    private static final String LOCATION_NAME = "a name";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final int LOCATION_REMAINING_CAPACITY = LOCATION_TOTAL_CAPACITY;
    private static final LocationPayload SUBSCRIPTION_LOCATION_PAYLOAD =
        new LocationPayload(LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY, LOCATION_REMAINING_CAPACITY);

    private static final PickupLocation SUBSCRIPTION_PICKUP_LOCATION = new PickupLocation(LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);
    private static final LocalDate SUBSCRIPTION_START_DATE = LocalDate.now();
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"),
        SUBSCRIPTION_START_DATE, SUBSCRIPTION_START_DATE.minusYears(1));
    private static final LunchboxType SUBSCRIPTION_LUNCHBOX_TYPE = LunchboxType.STANDARD;

    @Test
    public void whenUsingFrom_shouldReturnCorrectSubscriptionPayload() {
        SubscriptionPayload expectedSubscriptionPayload =
            new SubscriptionPayload(SUBSCRIPTION_ID, ORDERS_PAYLOAD, SUBSCRIPTION_FREQUENCY, SUBSCRIPTION_LOCATION_PAYLOAD, SUBSCRIPTION_START_DATE,
                SUBSCRIPTION_LUNCHBOX_TYPE, A_SEMESTER);
        Subscription subscription =
            new SubscriptionFixture().withSubscriptionId(SUBSCRIPTION_ID).withOrders(List.of(ORDER)).withFrequency(SUBSCRIPTION_FREQUENCY)
                .withPickupLocation(SUBSCRIPTION_PICKUP_LOCATION).withStartDate(SUBSCRIPTION_START_DATE).withLunchboxType(SUBSCRIPTION_LUNCHBOX_TYPE).build();

        SubscriptionPayload actualSubscriptionPayload = SubscriptionPayload.from(subscription, A_SEMESTER);

        assertEquals(expectedSubscriptionPayload, actualSubscriptionPayload);
    }
}
