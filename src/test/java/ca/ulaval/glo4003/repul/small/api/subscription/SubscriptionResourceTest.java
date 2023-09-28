package ca.ulaval.glo4003.repul.small.api.subscription;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.subscription.SubscriptionResource;
import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionCreatedResponse;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.application.subscription.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.fixture.SubscriptionRequestFixture;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionResourceTest {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private static final String SUBSCRIPTION_ID = UUID.randomUUID().toString();
    private static final SubscriptionsPayload A_SUBSCRIPTIONS_PAYLOAD = new SubscriptionsPayload(List.of());
    private static final String A_LOCATION_ID = "VACHON";
    private static final String A_DAY_OF_WEEK = DayOfWeek.MONDAY.toString();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST = new SubscriptionRequestFixture()
        .withLocationId(A_LOCATION_ID)
        .withDayOfWeek(A_DAY_OF_WEEK)
        .build();
    private static final UniqueIdentifier ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());

    @Mock
    private SubscriptionService subscriptionService;
    private SubscriptionResource subscriptionResource;

    @Mock
    private ContainerRequestContext containerRequestContext;

    @BeforeEach
    public void createSubscriptionResource() {
        given(containerRequestContext.getProperty(any())).willReturn(ACCOUNT_ID);
        subscriptionResource = new SubscriptionResource(subscriptionService);
    }

    @Test
    public void whenConfirmingLunchbox_shouldReturn200() {
        Response response = subscriptionResource.confirmLunchbox(containerRequestContext, SUBSCRIPTION_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenDecliningLunchbox_shouldReturn200() {
        Response response = subscriptionResource.declineLunchbox(containerRequestContext, SUBSCRIPTION_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldCreateSubscription() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID);
        given(subscriptionService.createSubscription(ACCOUNT_ID, new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK))).willReturn(A_SUBSCRIPTION_ID);
        SubscriptionQuery subscriptionQuery = new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK);

        subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        verify(subscriptionService).createSubscription(ACCOUNT_ID, subscriptionQuery);
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturn201() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID);
        given(subscriptionService.createSubscription(ACCOUNT_ID, new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK))).willReturn(A_SUBSCRIPTION_ID);
        Response response = subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturnSubscriptionId() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID);
        given(subscriptionService.createSubscription(ACCOUNT_ID, new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK))).willReturn(A_SUBSCRIPTION_ID);
        Response response = subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        assertEquals(A_SUBSCRIPTION_ID.value().toString(), ((SubscriptionCreatedResponse) response.getEntity()).subscriptionId());
    }

    @Test
    public void whenGettingSubscriptions_shouldGetAccountId() {
        when(subscriptionService.getSubscriptions(ACCOUNT_ID)).thenReturn(A_SUBSCRIPTIONS_PAYLOAD);

        subscriptionResource.getSubscriptions(containerRequestContext);

        verify(containerRequestContext).getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
    }

    @Test
    public void whenGetSubscriptions_shouldGetSubscriptions() {
        when(subscriptionService.getSubscriptions(ACCOUNT_ID)).thenReturn(A_SUBSCRIPTIONS_PAYLOAD);

        subscriptionResource.getSubscriptions(containerRequestContext);

        verify(subscriptionService).getSubscriptions(ACCOUNT_ID);
    }

    @Test
    public void whenGetSubscriptions_shouldReturn200() {
        when(subscriptionService.getSubscriptions(ACCOUNT_ID)).thenReturn(A_SUBSCRIPTIONS_PAYLOAD);

        Response response = subscriptionResource.getSubscriptions(containerRequestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
