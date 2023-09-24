package ca.ulaval.glo4003.repul.small.repul.api.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.subscription.SubscriptionResource;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SubscriptionResourceTest {
    private static final String SUBSCRIPTION_ID = "TODO";
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
}
