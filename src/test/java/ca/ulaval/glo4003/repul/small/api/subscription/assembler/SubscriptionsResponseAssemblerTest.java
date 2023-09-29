package ca.ulaval.glo4003.repul.small.api.subscription.assembler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.subscription.assembler.SubscriptionsResponseAssembler;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.fixture.SubscriptionFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionsResponseAssemblerTest {
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(new LocationId("VACHON"), "toto", 10);
    private static final Frequency A_FREQUENCY = new Frequency(DayOfWeek.MONDAY);
    private static final LocalDate A_DATE = LocalDate.now();
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"), A_DATE, A_DATE.minusYears(1));
    private static final LunchboxType A_LUNCHBOX_TYPE = LunchboxType.STANDARD;
    private static final Subscription A_SUBSCRIPTION = new SubscriptionFixture()
        .withSubscriptionId(A_SUBSCRIPTION_ID).withPickupLocation(A_PICKUP_LOCATION)
        .withFrequency(A_FREQUENCY).withLunchboxType(A_LUNCHBOX_TYPE).withStartDate(A_DATE).build();
    private static final SubscriptionsPayload SUBSCRIPTIONS_PAYLOAD = new SubscriptionsPayload(
        List.of(SubscriptionPayload.from(A_SUBSCRIPTION, A_SEMESTER)));

    private SubscriptionsResponseAssembler subscriptionResponseAssembler;

    @Test
    public void givenSubscriptionPayload_whenAssemblingSubscriptionsResponse_shouldReturnListOfSubscriptionResponse() {
        subscriptionResponseAssembler = new SubscriptionsResponseAssembler();

        List<SubscriptionResponse> subscriptionResponses = subscriptionResponseAssembler.toSubscriptionsResponse(SUBSCRIPTIONS_PAYLOAD);

        assertEquals(A_SUBSCRIPTION_ID.value().toString(), subscriptionResponses.get(0).subscriptionId());
        assertEquals(A_PICKUP_LOCATION.getLocationId().value(), subscriptionResponses.get(0).locationId());
        assertEquals(A_FREQUENCY.dayOfWeek().name(), subscriptionResponses.get(0).dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE.name(), subscriptionResponses.get(0).lunchboxType());
        assertEquals(A_DATE.toString(), subscriptionResponses.get(0).startDate());
    }
}
