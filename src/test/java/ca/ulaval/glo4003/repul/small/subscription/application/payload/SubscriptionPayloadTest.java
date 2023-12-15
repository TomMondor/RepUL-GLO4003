package ca.ulaval.glo4003.repul.small.subscription.application.payload;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionPayloadTest {
    private static final SubscriptionUniqueIdentifier SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;
    private static final MealKitType ORDER_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final Order ORDER =
        new OrderFixture().withMealKitType(ORDER_MEALKIT_TYPE).withDeliveryDate(ORDER_DELIVERY_DATE).withOrderStatus(ORDER_STATUS).build();
    private static final Optional<Frequency> SUBSCRIPTION_FREQUENCY = Optional.of(new Frequency(DayOfWeek.MONDAY));
    private static final Optional<DeliveryLocationId> LOCATION_ID = Optional.of(DeliveryLocationId.VACHON);
    private static final LocalDate SUBSCRIPTION_START_DATE = LocalDate.now();
    private static final Semester SEMESTER = new Semester(new SemesterCode("A23"), SUBSCRIPTION_START_DATE, SUBSCRIPTION_START_DATE.minusYears(1));
    private static final MealKitType SUBSCRIPTION_MEALKIT_TYPE = MealKitType.STANDARD;

    @Test
    public void whenUsingFrom_shouldReturnCorrectSubscriptionPayload() {
        SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload(
            SUBSCRIPTION_ID.getUUID().toString(),
            SUBSCRIPTION_FREQUENCY.get().dayOfWeek().toString(),
            LOCATION_ID.get().toString(),
            SUBSCRIPTION_START_DATE.toString(),
            SUBSCRIPTION_MEALKIT_TYPE.toString(),
            SEMESTER.toString()
        );
        Subscription subscription = new SubscriptionFixture().withSubscriptionId(SUBSCRIPTION_ID).withOrders(List.of(ORDER))
            .withFrequency(SUBSCRIPTION_FREQUENCY).withPickupLocationId(LOCATION_ID).withStartDate(SUBSCRIPTION_START_DATE)
            .withMealKitType(SUBSCRIPTION_MEALKIT_TYPE).withSemester(SEMESTER).build();

        SubscriptionPayload actualSubscriptionPayload = SubscriptionPayload.from(subscription);

        assertEquals(expectedSubscriptionPayload, actualSubscriptionPayload);
    }

    @Test
    public void givenNoFrequency_whenUsingFrom_shouldReturnSubscriptionPayloadWithCustomString() {
        SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload(
            SUBSCRIPTION_ID.getUUID().toString(),
            "Sporadic",
            LOCATION_ID.get().toString(),
            SUBSCRIPTION_START_DATE.toString(),
            SUBSCRIPTION_MEALKIT_TYPE.toString(),
            SEMESTER.toString()
        );
        Subscription subscription = new SubscriptionFixture().withSubscriptionId(SUBSCRIPTION_ID).withOrders(List.of(ORDER))
            .withFrequency(Optional.empty()).withPickupLocationId(LOCATION_ID).withStartDate(SUBSCRIPTION_START_DATE)
            .withMealKitType(SUBSCRIPTION_MEALKIT_TYPE).withSemester(SEMESTER).build();

        SubscriptionPayload actualSubscriptionPayload = SubscriptionPayload.from(subscription);

        assertEquals(expectedSubscriptionPayload, actualSubscriptionPayload);
    }

    @Test
    public void givenNoDeliveryLocationId_whenUsingFrom_shouldReturnSubscriptionPayloadWithCustomString() {
        SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload(
            SUBSCRIPTION_ID.getUUID().toString(),
            SUBSCRIPTION_FREQUENCY.get().dayOfWeek().toString(),
            "In kitchen pick up",
            SUBSCRIPTION_START_DATE.toString(),
            SUBSCRIPTION_MEALKIT_TYPE.toString(),
            SEMESTER.toString()
        );
        Subscription subscription = new SubscriptionFixture().withSubscriptionId(SUBSCRIPTION_ID).withOrders(List.of(ORDER))
            .withFrequency(SUBSCRIPTION_FREQUENCY).withPickupLocationId(Optional.empty()).withStartDate(SUBSCRIPTION_START_DATE)
            .withMealKitType(SUBSCRIPTION_MEALKIT_TYPE).withSemester(SEMESTER).build();

        SubscriptionPayload actualSubscriptionPayload = SubscriptionPayload.from(subscription);

        assertEquals(expectedSubscriptionPayload, actualSubscriptionPayload);
    }
}
