package ca.ulaval.glo4003.repul.small.subscription.api;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionRequestFixture;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionResourceTest {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private static final String SUBSCRIPTION_ID_STRING = UUID.randomUUID().toString();
    private static final SubscriptionsPayload A_SUBSCRIPTIONS_PAYLOAD = new SubscriptionsPayload(List.of());
    private static final DeliveryLocationId A_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST =
        new SubscriptionRequestFixture().withLocationId(A_LOCATION_ID.toString()).withDayOfWeek(A_DAY_OF_WEEK.toString()).build();
    private static final SubscriberUniqueIdentifier ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final LocalDate AN_ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus AN_ORDER_STATUS = OrderStatus.PENDING;
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final MealKitUniqueIdentifier AN_ORDER_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final OrdersPayload AN_ORDERS_PAYLOAD = new OrdersPayload(
        List.of(new OrderPayload(AN_ORDER_ID.toString(), A_MEAL_KIT_TYPE.toString(), AN_ORDER_DELIVERY_DATE.toString(), AN_ORDER_STATUS.toString())));
    private static final String PATH_TO_API = "/api/subscriptions/";
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("H24"), LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(3));
    private static final SubscriptionPayload A_SUBSCRIPTION_PAYLOAD =
        new SubscriptionPayload(A_SUBSCRIPTION_ID.getUUID().toString(), DayOfWeek.MONDAY.toString(), DeliveryLocationId.VACHON.toString(),
            LocalDate.now().toString(), MealKitType.STANDARD.toString(), A_SEMESTER.toString());

    private SubscriptionResource subscriptionResource;

    @Mock
    private SubscriberService subscriberService;
    @Mock
    private ContainerRequestContext containerRequestContext;

    @BeforeEach
    public void createSubscriptionResource() {
        given(containerRequestContext.getProperty(any())).willReturn(ACCOUNT_ID.getUUID().toString());
        subscriptionResource = new SubscriptionResource(subscriberService);
    }

    @Test
    public void whenConfirmingMealKit_shouldReturnNoContent() {
        Response response = subscriptionResource.confirm(containerRequestContext, SUBSCRIPTION_ID_STRING);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenDecliningMealKit_shouldReturnNoContent() {
        Response response = subscriptionResource.decline(containerRequestContext, SUBSCRIPTION_ID_STRING);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturn201() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID.getUUID().toString());
        given(subscriberService.createSubscription(any(SubscriptionQuery.class))).willReturn(
            A_SUBSCRIPTION_ID);

        Response response = subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturnSubscriptionId() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID.getUUID().toString());
        given(subscriberService.createSubscription(any(SubscriptionQuery.class))).willReturn(
            A_SUBSCRIPTION_ID);
        Response response = subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        assertEquals(PATH_TO_API + A_SUBSCRIPTION_ID.getUUID().toString(), response.getHeaderString("Location"));
    }

    @Test
    public void givenSporadicSubscription_whenCreatingSubscription_shouldReturn201() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID.getUUID().toString());
        given(subscriberService.createSubscription(any(SubscriptionQuery.class))).willReturn(A_SUBSCRIPTION_ID);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestFixture().withSubscriptionType("SPORADIC").build();

        Response response = subscriptionResource.createSubscription(containerRequestContext, subscriptionRequest);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenSporadicSubscription_whenCreatingSubscription_shouldReturnSubscriptionId() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID.getUUID().toString());
        given(subscriberService.createSubscription(any(SubscriptionQuery.class))).willReturn(A_SUBSCRIPTION_ID);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestFixture().withSubscriptionType("SPORADIC").build();

        Response response = subscriptionResource.createSubscription(containerRequestContext, subscriptionRequest);

        assertEquals(PATH_TO_API + A_SUBSCRIPTION_ID.getUUID().toString(), response.getHeaderString("Location"));
    }

    @Test
    public void whenGettingAllSubscriptions_shouldReturn200() {
        given(subscriberService.getAllSubscriptions(ACCOUNT_ID)).willReturn(A_SUBSCRIPTIONS_PAYLOAD);

        Response response = subscriptionResource.getAllSubscriptions(containerRequestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingSubscription_shouldReturn200() {
        given(subscriberService.getSubscription(ACCOUNT_ID, A_SUBSCRIPTION_ID)).willReturn(A_SUBSCRIPTION_PAYLOAD);

        Response response = subscriptionResource.getSubscription(containerRequestContext, A_SUBSCRIPTION_ID.getUUID().toString());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingMyOrders_shouldReturn200() {
        when(subscriberService.getCurrentOrders(any(SubscriberUniqueIdentifier.class))).thenReturn(AN_ORDERS_PAYLOAD);
        Response response = subscriptionResource.getCurrentOrders(containerRequestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingMyOrders_shouldReturnOrdersPayload() {
        when(subscriberService.getCurrentOrders(any(SubscriberUniqueIdentifier.class))).thenReturn(AN_ORDERS_PAYLOAD);

        Response response = subscriptionResource.getCurrentOrders(containerRequestContext);

        assertEquals(AN_ORDERS_PAYLOAD, response.getEntity());
    }
}
