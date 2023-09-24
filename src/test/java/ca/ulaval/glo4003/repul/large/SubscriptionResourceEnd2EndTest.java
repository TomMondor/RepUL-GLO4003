package ca.ulaval.glo4003.repul.large;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionResourceEnd2EndTest {
    private static final String BASE_URL = "http://localhost:8080";
    private static final String SUBSCRIPTION_ID = "TODO";
    private TestServer server;

    @BeforeEach
    public void startServer() throws Exception {
        server = new TestServer();
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenConfirmingLunchbox_shouldReturn200() {
        Response response = when().post(BASE_URL + "/api/subscriptions/" + SUBSCRIPTION_ID + "/confirm");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenDecliningLunchbox_shouldReturn200() {
        Response response = when().post(BASE_URL + "/api/subscriptions/" + SUBSCRIPTION_ID + "/decline");

        assertEquals(200, response.getStatusCode());
    }
}
