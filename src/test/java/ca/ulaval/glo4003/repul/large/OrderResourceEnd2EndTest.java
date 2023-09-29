package ca.ulaval.glo4003.repul.large;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.api.account.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.api.order.response.OrderResponse;
import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.fixture.RegistrationRequestFixture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderResourceEnd2EndTest {
    private static final String ACCOUNT_EMAIL = "myAccountEmail@ulaval.ca";
    private static final String ACCOUNT_PASSWORD = "secret123";
    private static final String ACCOUNT_IDUL = "ALMAT69";
    private static final String ACCOUNT_NAME = "Bob Math";
    private static final String ACCOUNT_BIRTHDATE = "1969-04-20";
    private static final String ACCOUNT_GENDER = "MAN";

    private static final ApplicationContext context = new DevApplicationContext();
    private static final String DEFAULT_OFFER_PATH = "api/orders";
    private static final String SUBSCRIPTION_DAY_OF_WEEK = "MONDAY";
    private static final String SUBSCRIPTION_LOCATION_ID = "VACHON";
    private static final String SUBSCRIPTION_LUNCHBOX_TYPE = LunchboxType.STANDARD.toString();

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        server = new ServerFixture(context);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenGettingMyOrders_shouldReturn200() {
        String accountToken = registerAndLoginAccount();

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(context.getURI() + DEFAULT_OFFER_PATH + "/me");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void givenAValidOrder_whenGettingMyOrders_shouldReturnOrder() {
        String accountToken = registerAndLoginAccount();
        givenAValidOrder(accountToken);

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(context.getURI() + DEFAULT_OFFER_PATH + "/me");
        JsonPath jsonPath = response.jsonPath();
        List<OrderResponse> orders = jsonPath.getList("$", OrderResponse.class);

        assertNotNull(orders.get(0).orderStatus());
        assertNotNull(orders.get(0).deliveryDate());
        assertNotNull(orders.get(0).recipeNames());
        assertNotNull(orders.get(0).recipeNames().get(0));
    }

    private void givenAValidOrder(String accountToken) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.dayOfWeek = SUBSCRIPTION_DAY_OF_WEEK;
        subscriptionRequest.locationId = SUBSCRIPTION_LOCATION_ID;
        subscriptionRequest.lunchboxType = SUBSCRIPTION_LUNCHBOX_TYPE;

        given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(subscriptionRequest)
            .post(context.getURI() + "api/subscriptions");
    }

    private String registerAndLoginAccount() {
        RegistrationRequest registrationRequest = createRegistrationRequestWithAccountInformation();
        LoginRequest loginRequest =
            new ca.ulaval.glo4003.identitymanagement.fixture.LoginRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).build();
        given().contentType("application/json").body(registrationRequest).post(context.getURI() + "api/accounts/register");
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(context.getURI() + "api/auth/login");
        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private RegistrationRequest createRegistrationRequestWithAccountInformation() {
        return new RegistrationRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).withBirthdate(ACCOUNT_BIRTHDATE)
            .withGender(ACCOUNT_GENDER).withName(ACCOUNT_NAME).withIdul(ACCOUNT_IDUL).build();
    }
}
