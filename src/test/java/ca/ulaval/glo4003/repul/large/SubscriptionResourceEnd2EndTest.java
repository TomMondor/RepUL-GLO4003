package ca.ulaval.glo4003.repul.large;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionResourceEnd2EndTest {
    private static final String SUBSCRIPTION_ID = "TODO";
    private static String baseUrl;
    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        ApplicationContext context = new DevApplicationContext();
        baseUrl = context.getURI();
        server = new ServerFixture(context);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenConfirmingLunchbox_shouldReturn200() {
        Response response = when().post(baseUrl + "api/subscriptions/" + SUBSCRIPTION_ID + "/confirm");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenDecliningLunchbox_shouldReturn200() {
        Response response = when().post(baseUrl + "api/subscriptions/" + SUBSCRIPTION_ID + "/decline");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGetSubscriptions_shouldReturn200() {
        Response response = when().get(baseUrl + "api/subscriptions");

        assertEquals(200, response.getStatusCode());
    }
}
