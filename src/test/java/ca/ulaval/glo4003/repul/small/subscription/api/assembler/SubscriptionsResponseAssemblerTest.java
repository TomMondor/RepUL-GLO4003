package ca.ulaval.glo4003.repul.small.subscription.api.assembler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.api.assembler.SubscriptionsResponseAssembler;
import ca.ulaval.glo4003.repul.subscription.api.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionsResponseAssemblerTest {
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final Frequency A_FREQUENCY = new Frequency(DayOfWeek.MONDAY);
    private static final LocalDate A_DATE = LocalDate.now();
    private static final MealKitType A_LUNCHBOX_TYPE = MealKitType.STANDARD;
    private static final Subscription A_SUBSCRIPTION =
        new SubscriptionFixture().withSubscriptionId(A_SUBSCRIPTION_ID).withPickupLocationId(A_DELIVERY_LOCATION_ID).withFrequency(A_FREQUENCY)
            .withMealKitType(A_LUNCHBOX_TYPE).withStartDate(A_DATE).build();
    private static final SubscriptionPayload SUBSCRIPTION_PAYLOAD = SubscriptionPayload.from(A_SUBSCRIPTION);
    private static final SubscriptionsPayload SUBSCRIPTIONS_PAYLOAD = new SubscriptionsPayload(List.of(SUBSCRIPTION_PAYLOAD));

    private SubscriptionsResponseAssembler subscriptionResponseAssembler;

    @Test
    public void givenSubscriptionsPayload_whenAssemblingSubscriptionsResponse_shouldReturnListOfSubscriptionResponse() {
        subscriptionResponseAssembler = new SubscriptionsResponseAssembler();

        List<SubscriptionResponse> subscriptionResponses = subscriptionResponseAssembler.toSubscriptionsResponse(SUBSCRIPTIONS_PAYLOAD);

        assertEquals(A_SUBSCRIPTION_ID.value().toString(), subscriptionResponses.get(0).subscriptionId());
        assertEquals(A_DELIVERY_LOCATION_ID.value(), subscriptionResponses.get(0).locationId());
        assertEquals(A_FREQUENCY.dayOfWeek().name(), subscriptionResponses.get(0).dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE.name(), subscriptionResponses.get(0).lunchboxType());
        assertEquals(A_DATE.toString(), subscriptionResponses.get(0).startDate());
    }

    @Test
    public void givenSubscriptionPayload_whenAssemblingSubscriptionResponse_shouldReturnSubscriptionResponse() {
        subscriptionResponseAssembler = new SubscriptionsResponseAssembler();

        SubscriptionResponse subscriptionResponse = subscriptionResponseAssembler.toSubscriptionResponse(SUBSCRIPTION_PAYLOAD);

        assertEquals(A_SUBSCRIPTION_ID.value().toString(), subscriptionResponse.subscriptionId());
        assertEquals(A_DELIVERY_LOCATION_ID.value(), subscriptionResponse.locationId());
        assertEquals(A_FREQUENCY.dayOfWeek().name(), subscriptionResponse.dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE.name(), subscriptionResponse.lunchboxType());
        assertEquals(A_DATE.toString(), subscriptionResponse.startDate());
    }
}
