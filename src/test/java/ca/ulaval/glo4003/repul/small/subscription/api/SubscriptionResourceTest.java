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
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionRequestFixture;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.subscription.api.response.OrderResponse;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;

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
    private static final String A_MEAL_KIT_TYPE_STRING = "STANDARD";
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST =
        new SubscriptionRequestFixture().withLocationId(A_LOCATION_ID).withDayOfWeek(A_DAY_OF_WEEK).build();
    private static final UniqueIdentifier ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory().generate();
    private static final LocalDate AN_ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus AN_ORDER_STATUS = OrderStatus.PENDING;
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final UniqueIdentifier AN_ORDER_ID = new UniqueIdentifierFactory().generate();
    private static final OrdersPayload AN_ORDERS_PAYLOAD =
        new OrdersPayload(List.of(new OrderPayload(AN_ORDER_ID, A_MEAL_KIT_TYPE, AN_ORDER_DELIVERY_DATE, AN_ORDER_STATUS)));
    private static final String PATH_TO_API = "/api/subscriptions/";
    private static final Frequency A_FREQUENCY = new Frequency(DayOfWeek.MONDAY);
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Here");
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("H24"), LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(3));
    private static final SubscriptionPayload A_SUBSCRIPTION_PAYLOAD =
        new SubscriptionPayload(ACCOUNT_ID, A_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(), MealKitType.STANDARD, A_SEMESTER);

    private SubscriptionResource subscriptionResource;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ContainerRequestContext containerRequestContext;

    @BeforeEach
    public void createSubscriptionResource() {
        given(containerRequestContext.getProperty(any())).willReturn(ACCOUNT_ID);
        subscriptionResource = new SubscriptionResource(subscriptionService);
    }

    @Test
    public void whenConfirmingMealKit_shouldConfirmMealKit() {
        subscriptionResource.confirmMealKit(containerRequestContext, A_SUBSCRIPTION_ID.value().toString());

        verify(subscriptionService).confirmNextMealKitForSubscription(ACCOUNT_ID, A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenConfirmingMealKit_shouldReturnNoContent() {
        Response response = subscriptionResource.confirmMealKit(containerRequestContext, SUBSCRIPTION_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenDecliningMealKit_shouldDeclineMealKit() {
        subscriptionResource.declineMealKit(containerRequestContext, A_SUBSCRIPTION_ID.value().toString());

        verify(subscriptionService).declineNextMealKitForSubscription(ACCOUNT_ID, A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenDecliningMealKit_shouldReturnNoContent() {
        Response response = subscriptionResource.declineMealKit(containerRequestContext, SUBSCRIPTION_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenCreatingSubscription_shouldCreateSubscription() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID);
        given(subscriptionService.createSubscription(ACCOUNT_ID, new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK, A_MEAL_KIT_TYPE_STRING))).willReturn(
            A_SUBSCRIPTION_ID);
        SubscriptionQuery subscriptionQuery = new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK, A_MEAL_KIT_TYPE_STRING);

        subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        verify(subscriptionService).createSubscription(ACCOUNT_ID, subscriptionQuery);
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturn201() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID);
        given(subscriptionService.createSubscription(ACCOUNT_ID, new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK, A_MEAL_KIT_TYPE_STRING))).willReturn(
            A_SUBSCRIPTION_ID);
        Response response = subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenValidRequest_whenCreatingSubscription_shouldReturnSubscriptionId() {
        given(containerRequestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(ACCOUNT_ID);
        given(subscriptionService.createSubscription(ACCOUNT_ID, new SubscriptionQuery(A_LOCATION_ID, A_DAY_OF_WEEK, A_MEAL_KIT_TYPE_STRING))).willReturn(
            A_SUBSCRIPTION_ID);
        Response response = subscriptionResource.createSubscription(containerRequestContext, A_SUBSCRIPTION_REQUEST);

        assertEquals(PATH_TO_API + A_SUBSCRIPTION_ID.value().toString(), response.getHeaderString("Location"));
    }

    @Test
    public void whenGettingSubscriptions_shouldGetSubscriptions() {
        when(subscriptionService.getSubscriptions(ACCOUNT_ID)).thenReturn(A_SUBSCRIPTIONS_PAYLOAD);

        subscriptionResource.getSubscriptions(containerRequestContext);

        verify(subscriptionService).getSubscriptions(ACCOUNT_ID);
    }

    @Test
    public void whenGettingSubscriptions_shouldReturn200() {
        when(subscriptionService.getSubscriptions(ACCOUNT_ID)).thenReturn(A_SUBSCRIPTIONS_PAYLOAD);

        Response response = subscriptionResource.getSubscriptions(containerRequestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingSubscriptionById_shouldGetSubscription() {
        when(subscriptionService.getSubscriptionById(ACCOUNT_ID, A_SUBSCRIPTION_ID)).thenReturn(A_SUBSCRIPTION_PAYLOAD);

        subscriptionResource.getSubscription(containerRequestContext, A_SUBSCRIPTION_ID.value().toString());

        verify(subscriptionService).getSubscriptionById(ACCOUNT_ID, A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenGettingSubscription_shouldReturn200() {
        when(subscriptionService.getSubscriptionById(ACCOUNT_ID, A_SUBSCRIPTION_ID)).thenReturn(A_SUBSCRIPTION_PAYLOAD);

        Response response = subscriptionResource.getSubscription(containerRequestContext, A_SUBSCRIPTION_ID.value().toString());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingMyOrders_shouldGetOrders() {
        when(subscriptionService.getCurrentOrders(any(UniqueIdentifier.class))).thenReturn(AN_ORDERS_PAYLOAD);
        subscriptionResource.getMyCurrentOrders(containerRequestContext);

        verify(subscriptionService).getCurrentOrders(any(UniqueIdentifier.class));
    }

    @Test
    public void whenGettingMyOrders_shouldReturn200() {
        when(subscriptionService.getCurrentOrders(any(UniqueIdentifier.class))).thenReturn(AN_ORDERS_PAYLOAD);
        Response response = subscriptionResource.getMyCurrentOrders(containerRequestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingMyOrders_shouldReturnOrdersResponse() {
        when(subscriptionService.getCurrentOrders(any(UniqueIdentifier.class))).thenReturn(AN_ORDERS_PAYLOAD);
        List<OrderResponse> expectedOrderResponses =
            List.of(new OrderResponse(A_MEAL_KIT_TYPE.toString(), AN_ORDER_DELIVERY_DATE.toString(), AN_ORDER_STATUS.toString()));

        Response response = subscriptionResource.getMyCurrentOrders(containerRequestContext);

        assertEquals(expectedOrderResponses, response.getEntity());
    }
}
