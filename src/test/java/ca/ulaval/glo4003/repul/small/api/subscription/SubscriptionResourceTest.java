package ca.ulaval.glo4003.repul.small.api.subscription;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.subscription.SubscriptionResource;
import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.dto.SubscriptionsDTO;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;
import ca.ulaval.glo4003.repul.fixture.SubscriptionRequestFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SubscriptionResourceTest {
    private static final String SUBSCRIPTION_ID = UUID.randomUUID().toString();
    private static final SubscriptionsDTO A_SUBSCRIPTIONS_DTO = new SubscriptionsDTO(List.of());
    private static final String A_LOCATION_ID = "VACHON";
    private static final String A_DAY_OF_WEEK = DayOfWeek.MONDAY.toString();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST = new SubscriptionRequestFixture()
        .withLocationId(A_LOCATION_ID)
        .withDayOfWeek(A_DAY_OF_WEEK)
        .build();

    private SubscriptionService subscriptionService;
    private SubscriptionResource subscriptionResource;

    @BeforeEach
    public void createSubscriptionResource() {
        subscriptionService = mock(SubscriptionService.class);
        subscriptionResource = new SubscriptionResource(subscriptionService);
    }

    @Test
    public void whenConfirmingLunchbox_shouldReturn200() {
        Response response = subscriptionResource.confirmLunchbox(SUBSCRIPTION_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenDecliningLunchbox_shouldReturn200() {
        Response response = subscriptionResource.declineLunchbox(SUBSCRIPTION_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldCreateSubscription() {
        SubscriptionParams subscriptionParams = new SubscriptionParams(A_LOCATION_ID, A_DAY_OF_WEEK);
        when(subscriptionService.getSubscriptions()).thenReturn(A_SUBSCRIPTIONS_DTO);

        subscriptionResource.createSubscription(A_SUBSCRIPTION_REQUEST);

        verify(subscriptionService).createSubscription(subscriptionParams);
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturn200() {
        when(subscriptionService.getSubscriptions()).thenReturn(A_SUBSCRIPTIONS_DTO);

        Response response = subscriptionResource.createSubscription(A_SUBSCRIPTION_REQUEST);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGetSubscriptions_shouldGetSubscriptions() {
        when(subscriptionService.getSubscriptions()).thenReturn(A_SUBSCRIPTIONS_DTO);

        subscriptionResource.getSubscriptions();

        verify(subscriptionService).getSubscriptions();
    }

    @Test
    public void whenGetSubscriptions_shouldReturn200() {
        when(subscriptionService.getSubscriptions()).thenReturn(A_SUBSCRIPTIONS_DTO);

        Response response = subscriptionResource.getSubscriptions();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
