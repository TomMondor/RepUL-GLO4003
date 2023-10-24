package ca.ulaval.glo4003.repul.large.cooking;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.TestApplicationContext;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MealKitResourceEnd2EndTest {
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
    public void whenGettingMealKitsToCook_shouldReturn200() {
        String accountToken = loginAsACook();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:toCook");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingMealKitsToCook_shouldReturnMealKitsToCook() {
        String accountToken = loginAsACook();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:toCook");
        ToCookResponse responseBody = response.getBody().as(ToCookResponse.class);

        assertFalse(responseBody.mealKits().isEmpty());
        assertFalse(responseBody.totalIngredients().isEmpty());
    }

    private String loginAsACook() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestApplicationContext.COOK_EMAIL).withPassword(TestApplicationContext.COOK_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }
}
