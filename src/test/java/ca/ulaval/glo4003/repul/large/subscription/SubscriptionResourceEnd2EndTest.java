package ca.ulaval.glo4003.repul.large.subscription;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.TestIdentityManagementSeed;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.identitymanagement.LoginRequestFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionRequestFixture;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriptionResourceEnd2EndTest {
    private static final int SUBSCRIPTION_ID_LENGTH = 14;
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST = new SubscriptionRequestFixture().build();
    private static final SubscriptionRequest A_SPORADIC_SUBSCRIPTION_REQUEST = new SubscriptionRequestFixture().withSubscriptionType("SPORADIC").build();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST_STARTING_IN_THREE_DAYS =
        new SubscriptionRequestFixture().withDayOfWeek(LocalDate.now().plusDays(3).getDayOfWeek().toString()).build();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST_STARTING_IN_FIVE_DAYS =
        new SubscriptionRequestFixture().withDayOfWeek(LocalDate.now().plusDays(5).getDayOfWeek().toString()).build();
    private static final ApplicationContext CONTEXT = new TestApplicationContext();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        System.setOut(new PrintStream(outputStream));
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        RestAssured.reset();
        System.setOut(System.out);
        server.stop();
    }

    @Test
    public void whenSubscribing_shouldReturn201() {
        String accountToken = login();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(A_SUBSCRIPTION_REQUEST)
            .post(CONTEXT.getURI() + "subscriptions");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenSubscribing_shouldReturnSubscriptionIdInLocationHeader() {
        String accountToken = login();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(A_SUBSCRIPTION_REQUEST)
            .post(CONTEXT.getURI() + "subscriptions");
        String locationHeader = response.getHeader("Location");

        assertFalse(locationHeader.isEmpty());
    }

    @Test
    public void whenSubscribingToSporadicSubscription_shouldReturn201() {
        String accountToken = login();

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).body(A_SPORADIC_SUBSCRIPTION_REQUEST).header("Authorization", "Bearer " + accountToken)
                .post(CONTEXT.getURI() + "subscriptions");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenSubscribingToSporadicSubscription_shouldReturnSubscriptionIdInLocationHeader() {
        String accountToken = login();

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).body(A_SPORADIC_SUBSCRIPTION_REQUEST).header("Authorization", "Bearer " + accountToken)
                .post(CONTEXT.getURI() + "subscriptions");
        String locationHeader = response.getHeader("Location");

        assertFalse(locationHeader.isEmpty());
    }

    @Test
    public void whenGettingSubscriptions_shouldReturn200() {
        String accountToken = login();

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingSubscriptions_shouldReturnSubscriptions() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST);

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions");
        SubscriptionsPayload responseBody = response.getBody().as(SubscriptionsPayload.class);
        Optional<SubscriptionPayload> createdSubscriptionResponse =
            responseBody.subscriptions().stream().filter(subscription -> subscription.subscriptionId().equals(subscriptionId)).findFirst();

        assertTrue(createdSubscriptionResponse.isPresent());
        assertEquals(A_SUBSCRIPTION_REQUEST.dayOfWeek, createdSubscriptionResponse.get().weeklyOccurence());
        assertEquals(A_SUBSCRIPTION_REQUEST.locationId, createdSubscriptionResponse.get().deliveryLocationId());
        assertEquals(A_SUBSCRIPTION_REQUEST.mealKitType, createdSubscriptionResponse.get().mealKitType());
        assertEquals(LocalDate.now().toString(), createdSubscriptionResponse.get().startDate());
    }

    @Test
    public void whenGettingSubscription_shouldReturn200() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST);

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .get(CONTEXT.getURI() + "subscriptions/" + subscriptionId);

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingSubscription_shouldReturnSubscription() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST);
        String url = CONTEXT.getURI() + "subscriptions/" + subscriptionId;
        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(url);
        SubscriptionPayload responseBody = response.getBody().as(SubscriptionPayload.class);

        assertEquals(subscriptionId, responseBody.subscriptionId());
        assertEquals(A_SUBSCRIPTION_REQUEST.dayOfWeek, responseBody.weeklyOccurence());
        assertEquals(A_SUBSCRIPTION_REQUEST.locationId, responseBody.deliveryLocationId());
        assertEquals(A_SUBSCRIPTION_REQUEST.mealKitType, responseBody.mealKitType());
        assertEquals(LocalDate.now().toString(), responseBody.startDate());
    }

    @Test
    public void whenConfirmingCurrentOrder_shouldReturn204() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_IN_THREE_DAYS);

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "subscriptions/" + subscriptionId + ":confirm");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void givenSporadicSubscription_whenConfirmingCurrentOrder_shouldReturn204() {
        String accountToken = login();
        String subscriptionId = createSporadicSubscription(accountToken);

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "subscriptions/" + subscriptionId + ":confirm");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenDecliningCurrentOrder_shouldReturn204() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_IN_THREE_DAYS);

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "subscriptions/" + subscriptionId + ":decline");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturn200() {
        String accountToken = login();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .get(CONTEXT.getURI() + "subscriptions:currentOrders");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnCurrentOrders() {
        String accountToken = login();
        createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_IN_FIVE_DAYS);

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .get(CONTEXT.getURI() + "subscriptions:currentOrders");
        OrdersPayload responseBody = response.getBody().as(OrdersPayload.class);
        Optional<OrderPayload> createdOrderResponse =
            responseBody.orders().stream().filter(order -> order.deliveryDate().equals(LocalDate.now().plusDays(5).toString())).findFirst();

        assertTrue(createdOrderResponse.isPresent());
        assertEquals(OrderStatus.PENDING.toString(), createdOrderResponse.get().orderStatus());
        assertEquals(A_SUBSCRIPTION_REQUEST_STARTING_IN_FIVE_DAYS.mealKitType, createdOrderResponse.get().mealKitType());
        assertEquals(LocalDate.now().plusDays(5).toString(), createdOrderResponse.get().deliveryDate());
    }

    private String login() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestIdentityManagementSeed.CLIENT_EMAIL).withPassword(TestIdentityManagementSeed.CLIENT_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private String createSubscription(String accountToken, SubscriptionRequest subscriptionRequest) {
        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(subscriptionRequest)
            .post(CONTEXT.getURI() + "subscriptions");
        String location = response.getHeader("Location");
        return location.substring(location.indexOf("subscriptions/") + SUBSCRIPTION_ID_LENGTH);
    }

    private String createSporadicSubscription(String accountToken) {
        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(A_SPORADIC_SUBSCRIPTION_REQUEST)
                .post(CONTEXT.getURI() + "subscriptions");
        String location = response.getHeader("Location");
        return location.substring(location.indexOf("subscriptions/") + SUBSCRIPTION_ID_LENGTH);
    }
}
