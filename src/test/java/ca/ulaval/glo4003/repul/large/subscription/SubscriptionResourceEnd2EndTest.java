package ca.ulaval.glo4003.repul.large.subscription;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.TestApplicationContext;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.subscription.api.response.OrderResponse;
import ca.ulaval.glo4003.repul.subscription.api.response.SubscriptionCreatedResponse;
import ca.ulaval.glo4003.repul.subscription.api.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionResourceEnd2EndTest {
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST = new SubscriptionRequestFixture().build();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST_STARTING_IN_THREE_DAYS =
        new SubscriptionRequestFixture().withDayOfWeek(LocalDate.now().plusDays(3).getDayOfWeek().toString()).build();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST_STARTING_IN_FIVE_DAYS =
        new SubscriptionRequestFixture().withDayOfWeek(LocalDate.now().plusDays(5).getDayOfWeek().toString()).build();
    private static final SubscriptionRequest A_SUBSCRIPTION_REQUEST_STARTING_TODAY =
        new SubscriptionRequestFixture().withDayOfWeek(LocalDate.now().getDayOfWeek().toString()).build();
    private static final ApplicationContext CONTEXT = new TestApplicationContext();

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        RestAssured.reset();
        server.stop();
    }

    @Test
    public void whenSubscribing_shouldReturn201() {
        String accountToken = login();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(A_SUBSCRIPTION_REQUEST)
            .post(CONTEXT.getURI() + "subscriptions");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenSubscribing_shouldReturnSubscriptionId() {
        String accountToken = login();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(A_SUBSCRIPTION_REQUEST)
            .post(CONTEXT.getURI() + "subscriptions");
        SubscriptionCreatedResponse responseBody = response.getBody().as(SubscriptionCreatedResponse.class);

        assertNotNull(responseBody.subscriptionId());
    }

    @Test
    public void whenGettingSubscriptions_shouldReturn200() {
        String accountToken = login();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingSubscriptions_shouldReturnSubscriptions() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST);

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions");
        List<SubscriptionResponse> responseBody = Arrays.asList(response.getBody().as(SubscriptionResponse[].class));
        Optional<SubscriptionResponse> createdSubscriptionResponse =
            responseBody.stream().filter(subscription -> subscription.subscriptionId().equals(subscriptionId)).findFirst();

        assertTrue(createdSubscriptionResponse.isPresent());
        assertEquals(A_SUBSCRIPTION_REQUEST.dayOfWeek, createdSubscriptionResponse.get().dayOfWeek());
        assertEquals(A_SUBSCRIPTION_REQUEST.locationId, createdSubscriptionResponse.get().locationId());
        assertEquals(A_SUBSCRIPTION_REQUEST.lunchboxType, createdSubscriptionResponse.get().lunchboxType());
        assertEquals(LocalDate.now().toString(), createdSubscriptionResponse.get().startDate());
    }

    @Test
    public void whenConfirmingCurrentOrder_shouldReturn204() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_IN_THREE_DAYS);

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "subscriptions/" + subscriptionId + ":confirm");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenDecliningCurrentOrder_shouldReturn204() {
        String accountToken = login();
        String subscriptionId = createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_IN_THREE_DAYS);

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "subscriptions/" + subscriptionId + ":decline");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturn200() {
        String accountToken = login();

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions:currentOrders");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnCurrentOrders() {
        String accountToken = login();
        createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_IN_FIVE_DAYS);

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions:currentOrders");
        List<OrderResponse> responseBody = Arrays.asList(response.getBody().as(OrderResponse[].class));
        Optional<OrderResponse> createdOrderResponse =
            responseBody.stream().filter(order -> order.deliveryDate().equals(LocalDate.now().plusDays(5).toString())).findFirst();

        assertTrue(createdOrderResponse.isPresent());
        assertEquals(OrderStatus.PENDING.toString(), createdOrderResponse.get().orderStatus());
        assertEquals(A_SUBSCRIPTION_REQUEST_STARTING_IN_FIVE_DAYS.lunchboxType, createdOrderResponse.get().mealKitType());
        assertEquals(LocalDate.now().plusDays(5).toString(), createdOrderResponse.get().deliveryDate());
    }

    @Test
    public void givenOrderInLessThan48Hours_whenGettingCurrentOrders_shouldReturnDeclinedOrder() {
        String accountToken = login();
        createSubscription(accountToken, A_SUBSCRIPTION_REQUEST_STARTING_TODAY);

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "subscriptions:currentOrders");
        List<OrderResponse> responseBody = Arrays.asList(response.getBody().as(OrderResponse[].class));
        Optional<OrderResponse> createdOrderResponse =
            responseBody.stream().filter(order -> order.deliveryDate().equals(LocalDate.now().toString())).findFirst();

        assertTrue(createdOrderResponse.isPresent());
        assertEquals(OrderStatus.DECLINED.toString(), createdOrderResponse.get().orderStatus());
    }

    private String login() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestApplicationContext.CLIENT_EMAIL).withPassword(TestApplicationContext.CLIENT_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private String createSubscription(String accountToken, SubscriptionRequest subscriptionRequest) {
        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(subscriptionRequest)
            .post(CONTEXT.getURI() + "subscriptions");
        SubscriptionCreatedResponse responseBody = response.getBody().as(SubscriptionCreatedResponse.class);

        return responseBody.subscriptionId();
    }
}