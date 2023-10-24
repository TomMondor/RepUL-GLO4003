package ca.ulaval.glo4003.repul.large.health;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.TestApplicationContext;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.health.api.dto.HealthDto;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthResourceEnd2EndTest {
    private static final ApplicationContext context = new TestApplicationContext();

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
    public void whenGettingHealth_shouldReturn200() {
        Response response = given().contentType("application/json").get(context.getURI() + "health");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingHealth_shouldReturnEverythingIsFineMessage() {
        String expectedMessage = "OK - Everything is alright!";

        Response response = given().contentType("application/json").get(context.getURI() + "health");
        HealthDto healthDto = response.getBody().as(HealthDto.class);

        assertEquals(expectedMessage, healthDto.status());
    }
}
