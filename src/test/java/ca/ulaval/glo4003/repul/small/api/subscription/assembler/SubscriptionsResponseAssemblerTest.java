package ca.ulaval.glo4003.repul.small.api.subscription.assembler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.subscription.assembler.SubscriptionsResponseAssembler;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.application.subscription.dto.SubscriptionsDTO;
import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.fixture.SubscriptionFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionsResponseAssemblerTest {
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(new LocationId("VACHON"), "toto", 10);
    private static final Frequency A_FREQUENCY = new Frequency(DayOfWeek.MONDAY);
    private static final LocalDate A_DATE = LocalDate.now();
    private static final LunchboxType A_LUNCHBOX_TYPE = LunchboxType.STANDARD;
    private static final SubscriptionsDTO A_SUBSCRIPTION_DTO = new SubscriptionsDTO(List.of(
        new SubscriptionFixture().withPickupLocation(A_PICKUP_LOCATION).withFrequency(A_FREQUENCY).withLunchboxType(A_LUNCHBOX_TYPE).withStartDate(A_DATE)
            .build()));

    private SubscriptionsResponseAssembler subscriptionResponseAssembler;

    @Test
    public void givenSubscriptionDTO_whenToSubscriptionsResponse_shouldReturnListOfSubscriptionResponse() {
        subscriptionResponseAssembler = new SubscriptionsResponseAssembler();

        List<SubscriptionResponse> subscriptionResponses = subscriptionResponseAssembler.toSubscriptionsResponse(A_SUBSCRIPTION_DTO);

        assertEquals(A_PICKUP_LOCATION.getLocationId().value(), subscriptionResponses.get(0).locationId());
        assertEquals(A_FREQUENCY.dayOfWeek().name(), subscriptionResponses.get(0).dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE.name(), subscriptionResponses.get(0).lunchboxType());
        assertEquals(A_DATE.toString(), subscriptionResponses.get(0).startDate());
    }
}
