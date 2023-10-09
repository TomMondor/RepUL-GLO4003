package ca.ulaval.glo4003.repul.large;

import java.util.ArrayList;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.api.lunchbox.response.ToCookResponse;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.LoginRequestFixture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunchboxResourceEnd2EndTest {
    private static final ApplicationContext context = new DevApplicationContext();
    private static final String COOK_ACCOUNT_EMAIL = "cook@ulaval.ca";
    private static final String COOK_ACCOUNT_PASSWORD = "cook";

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
    public void whenGettingLunchboxToCook_shouldReturn200WithEmptyLunchboxAndIngredient() {
        String accountToken = loginCookAccount();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(context.getURI() + "api/lunchboxes/to-cook");

        assertEquals(200, response.getStatusCode());
        assertEquals(new ArrayList<Lunchbox>(), response.getBody().as(ToCookResponse.class).lunchboxes());
        assertEquals(new ArrayList<Ingredient>(), response.getBody().as(ToCookResponse.class).totalIngredients());
    }

    private String loginCookAccount() {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(COOK_ACCOUNT_EMAIL).withPassword(COOK_ACCOUNT_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(context.getURI() + "api/auth/login");
        return loginResponse.getBody().as(LoginResponse.class).token();
    }
}
